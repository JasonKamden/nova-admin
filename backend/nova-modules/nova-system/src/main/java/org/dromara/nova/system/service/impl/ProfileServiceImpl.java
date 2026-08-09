package org.dromara.nova.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.ContextType;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.storage.service.StorageService;
import org.dromara.nova.system.dto.request.ProfilePasswordReqDto;
import org.dromara.nova.system.dto.request.ProfileUpdateReqDto;
import org.dromara.nova.system.dto.response.ProfileRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.entity.UserEntity;
import org.dromara.nova.system.mapper.UserMapper;
import org.dromara.nova.system.model.ProfileAvatar;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.service.ProfileService;
import org.dromara.nova.system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;

/**
 * 个人中心。
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final UserMapper userMapper;
    private final Converter converter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final OnlineUserService onlineUserService;

    /**
     * 查询当前登录用户个人中心聚合信息。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public ProfileRespDto get() {
        var c = LoginUserUtils.getLoginUser();
        UserEntity u = requireUser();
        List<RoleSimpleRespDto> roles = c.contextType() == ContextType.TENANT ? userService.roles(u.getId()) : List.of();
        return new ProfileRespDto(u.getId(), u.getUsername(), u.getNickname(), u.getAvatar() == null ? null : "/api/profile/avatar", u.getGender(), u.getPhone(), u.getEmail(), u.getBio(), u.getStatus(), Boolean.TRUE.equals(u.getPlatformAdmin()), c.contextType(), c.tenantId(), c.tenantName(), c.departmentId(), c.departmentName(), roles, u.getCreateTime(), u.getLastLoginTime(), u.getLastLoginIp());
    }

    /**
     * 更新业务数据，执行状态、Tenant、权限与并发规则校验。
     *
     * @param req ProfileUpdateReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "PROFILE", type = "UPDATE", description = "修改个人资料")
    public ProfileRespDto update(ProfileUpdateReqDto req) {
        UserEntity u = requireUser();
        converter.convert(req, u);
        AuditEntitySupport.updated(u, u.getId());
        userMapper.update(u);
        return get();
    }

    /**
     * 校验旧密码并修改当前用户密码，成功后使会话失效。
     *
     * @param req ProfilePasswordReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     */
    @Override
    @OperationAudit(module = "PROFILE", type = "PASSWORD", description = "修改个人密码")
    public void changePassword(ProfilePasswordReqDto req) {
        UserEntity u = requireUser();
        if (!passwordEncoder.matches(req.oldPassword(), u.getPassword()))
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "原密码错误");
        u.setPassword(passwordEncoder.encode(req.newPassword()));
        AuditEntitySupport.updated(u, u.getId());
        userMapper.update(u);
        String token = StpUtil.getTokenValue();
        onlineUserService.invalidateUsers(List.of(u.getId()));
        if (token != null) onlineUserService.logout(token);
        StpUtil.logout();
    }

    /**
     * 更新当前用户头像并返回最新个人资料。
     *
     * @param file 上传文件内容
     * @return 方法处理结果。
     */
    @Override
    @OperationAudit(module = "PROFILE", type = "AVATAR", description = "修改头像")
    public String updateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException(CommonResultCode.BAD_REQUEST, "头像不能为空");
        if (file.getSize() > 5L * 1024 * 1024)
            throw new BusinessException(CommonResultCode.FILE_ERROR, "头像不能超过 5MB");
        if (!IMAGE_TYPES.contains(file.getContentType()))
            throw new BusinessException(CommonResultCode.FILE_ERROR, "仅支持 JPG/PNG/WebP");
        validateImage(file);
        UserEntity u = requireUser();
        String ext = switch (file.getContentType()) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
        String path = "avatars/" + u.getId() + "/" + UUID.randomUUID() + "." + ext;
        String old = u.getAvatar();
        try {
            storageService.store(path, file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "头像上传失败");
        }
        u.setAvatar(path);
        AuditEntitySupport.updated(u, u.getId());
        userMapper.update(u);
        if (old != null && !old.isBlank()) {
            try {
                storageService.delete(old);
            } catch (Exception ignored) {
            }
        }
        return "/api/profile/avatar";
    }

    /**
     * 打开当前用户头像对象流供 Controller 预览。
     *
     * @return 方法处理结果。
     */
    @Override
    public ProfileAvatar openAvatar() {
        UserEntity u = requireUser();
        if (u.getAvatar() == null || u.getAvatar().isBlank())
            throw new BusinessException(CommonResultCode.NOT_FOUND, "未设置头像");
        try {
            String type = u.getAvatar().endsWith(".png") ? "image/png" : u.getAvatar().endsWith(".webp") ? "image/webp" : "image/jpeg";
            java.io.InputStream input = storageService.open(u.getAvatar());
            return new ProfileAvatar(type, -1, input);
        } catch (Exception e) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "头像读取失败");
        }
    }

    /**
     * 校验头像 MIME 与文件签名一致，拒绝伪造图片类型。
     *
     * @param file 上传文件内容
     */
    private void validateImage(MultipartFile file) {
        try {
            byte[] b = file.getInputStream().readNBytes(16);
            boolean ok = switch (file.getContentType()) {
                case "image/jpeg" ->
                        b.length >= 3 && (b[0] & 0xff) == 0xff && (b[1] & 0xff) == 0xd8 && (b[2] & 0xff) == 0xff;
                case "image/png" ->
                        b.length >= 8 && (b[0] & 0xff) == 0x89 && b[1] == 0x50 && b[2] == 0x4e && b[3] == 0x47;
                case "image/webp" ->
                        b.length >= 12 && new String(b, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF") && new String(b, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP");
                default -> false;
            };
            if (!ok) throw new BusinessException(CommonResultCode.FILE_ERROR, "头像文件签名与 MIME 不一致");
        } catch (java.io.IOException e) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "头像读取失败");
        }
    }

    /**
     * 加载有效用户，不存在时抛出业务异常。
     *
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private UserEntity requireUser() {
        Long id = LoginUserUtils.getUserId();
        UserEntity u = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.ID.eq(id)).and(USER_ENTITY.DELETED.eq(false)));
        if (u == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "用户不存在");
        return u;
    }
}
