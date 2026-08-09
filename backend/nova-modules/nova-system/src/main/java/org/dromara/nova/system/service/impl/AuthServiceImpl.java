package org.dromara.nova.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.Status;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.system.dto.request.LoginReqDto;
import org.dromara.nova.system.dto.response.LoginRespDto;
import org.dromara.nova.system.dto.response.MenuRespDto;
import org.dromara.nova.system.entity.UserEntity;
import org.dromara.nova.system.mapper.UserMapper;
import org.dromara.nova.system.security.LoginAttemptGuard;
import org.dromara.nova.system.service.AuthService;
import org.dromara.nova.system.service.LoginLogService;
import org.dromara.nova.system.service.MenuService;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.support.LoginContextAssembler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;

/**
 * 账号密码认证并初始化可信 Context。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final LoginContextAssembler loginContextAssembler;
    private final LoginLogService loginLogService;
    private final OnlineUserService onlineUserService;
    private final MenuService menuService;
    private final LoginAttemptGuard loginAttemptGuard;

    /**
     * 校验登录账号、密码、用户状态和 Tenant 可用性，并创建 Sa-Token 登录会话。
     *
     * @param request LoginReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 业务响应 DTO。
     */
    @Override
    public LoginRespDto login(LoginReqDto request) {
        loginAttemptGuard.check(request.username());
        UserEntity user = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.USERNAME.eq(request.username())).and(USER_ENTITY.DELETED.eq(false)));
        if (user == null) {
            loginAttemptGuard.failure(request.username());
            loginLogService.record(null, request.username(), null, null, false, "账号或密码错误");
            log.warn("登录失败 username={} reason=INVALID_CREDENTIALS", request.username());
            throw new BusinessException(CommonResultCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (!Status.enabled(user.getStatus())) {
            loginLogService.record(user.getId(), user.getUsername(), null, null, false, "账号已禁用");
            log.warn("登录失败 userId={} username={} reason=USER_DISABLED", user.getId(), user.getUsername());
            throw new BusinessException(CommonResultCode.FORBIDDEN, "账号已被禁用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            loginAttemptGuard.failure(request.username());
            loginLogService.record(user.getId(), user.getUsername(), null, null, false, "账号或密码错误");
            log.warn("登录失败 userId={} username={} reason=INVALID_CREDENTIALS", user.getId(), user.getUsername());
            throw new BusinessException(CommonResultCode.UNAUTHORIZED, "账号或密码错误");
        }
        loginAttemptGuard.success(request.username());
        CurrentLoginUser loginUser;
        if (Boolean.TRUE.equals(user.getPlatformAdmin())) loginUser = loginContextAssembler.platform(user);
        else {
            loginUser = null;
            for (var membership : loginContextAssembler.memberships(user.getId())) {
                try {
                    loginUser = loginContextAssembler.tenant(user, membership.getTenantId());
                    break;
                } catch (BusinessException ignored) {
                }
            }
            if (loginUser == null) {
                loginLogService.record(user.getId(), user.getUsername(), null, null, false, "无可用 Tenant");
                log.warn("登录失败 userId={} username={} reason=NO_AVAILABLE_TENANT", user.getId(), user.getUsername());
                throw new BusinessException(CommonResultCode.FORBIDDEN, "当前账号没有可用 Tenant");
            }
        }
        StpUtil.login(user.getId());
        LoginUserUtils.saveLoginUser(loginUser);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(org.dromara.nova.common.web.util.IpUtils.currentIp());
        userMapper.update(user);
        String token = StpUtil.getTokenValue();
        onlineUserService.login(token, loginUser);
        loginLogService.record(user.getId(), user.getUsername(), loginUser.contextType().name(), loginUser.tenantId(), true, null);
        log.info("登录成功 userId={} username={} contextType={} tenantId={}", user.getId(), user.getUsername(), loginUser.contextType(), loginUser.tenantId());
        return new LoginRespDto(token, user.getId(), user.getUsername(), loginUser.contextType(), loginUser.tenantId());
    }

    /**
     * 注销当前登录会话并清理服务端会话状态。
     */
    @Override
    public void logout() {
        CurrentLoginUser loginUser = null;
        try {
            loginUser = LoginUserUtils.getLoginUser();
        } catch (Exception ignored) {
        }
        String token = StpUtil.getTokenValue();
        if (token != null) onlineUserService.logout(token);
        StpUtil.logout();
        if (loginUser != null)
            log.info("退出登录 userId={} username={} contextType={} tenantId={}", loginUser.userId(), loginUser.username(), loginUser.contextType(), loginUser.tenantId());
    }

    /**
     * 查询当前登录用户的安全会话信息。
     *
     * @return 方法处理结果。
     */
    @Override
    public CurrentLoginUser currentUser() {
        return LoginUserUtils.getLoginUser();
    }

    /**
     * 查询当前用户在当前 Context 下可访问的动态菜单。
     *
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<MenuRespDto> currentMenus() {
        return menuService.currentUserTree();
    }

    /**
     * 查询当前用户在当前 Context 下的按钮权限编码。
     *
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    @Override
    public List<String> currentPermissions() {
        return LoginUserUtils.getLoginUser().permissions();
    }
}
