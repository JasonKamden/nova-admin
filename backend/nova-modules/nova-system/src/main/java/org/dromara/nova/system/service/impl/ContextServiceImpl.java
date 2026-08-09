package org.dromara.nova.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.enums.Status;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.system.dto.response.ContextOptionsRespDto;
import org.dromara.nova.system.dto.response.ContextTenantOptionRespDto;
import org.dromara.nova.system.dto.response.CurrentContextRespDto;
import org.dromara.nova.system.entity.TenantEntity;
import org.dromara.nova.system.entity.UserEntity;
import org.dromara.nova.system.entity.UserTenantEntity;
import org.dromara.nova.system.mapper.TenantMapper;
import org.dromara.nova.system.mapper.UserMapper;
import org.dromara.nova.system.service.ContextService;
import org.dromara.nova.system.service.OnlineUserService;
import org.dromara.nova.system.support.LoginContextAssembler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.dromara.nova.system.entity.table.TenantEntityTableDef.TENANT_ENTITY;
import static org.dromara.nova.system.entity.table.UserEntityTableDef.USER_ENTITY;

/**
 * Context 查询和切换。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContextServiceImpl implements ContextService {
    private final UserMapper userMapper;
    private final TenantMapper tenantMapper;
    private final LoginContextAssembler loginContextAssembler;
    private final Converter converter;
    private final OnlineUserService onlineUserService;

    /**
     * 查询当前 PLATFORM/TENANT 运行 Context。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public CurrentContextRespDto current() {
        var u = LoginUserUtils.getLoginUser();
        return converter.convert(u, CurrentContextRespDto.class);
    }

    /**
     * 查询当前用户允许切换的 Context 选项。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public ContextOptionsRespDto options() {
        var u = LoginUserUtils.getLoginUser();
        List<ContextTenantOptionRespDto> list = new ArrayList<>();
        if (u.platformAdmin())
            tenantMapper.selectListByQuery(QueryWrapper.create().where(TENANT_ENTITY.DELETED.eq(false)).and(TENANT_ENTITY.STATUS.eq(1)).orderBy(TENANT_ENTITY.ID.desc())).stream().filter(this::usable).forEach(t -> list.add(converter.convert(t, ContextTenantOptionRespDto.class)));
        else for (UserTenantEntity m : loginContextAssembler.memberships(u.userId())) {
            TenantEntity t = tenantMapper.selectOneById(m.getTenantId());
            if (usable(t)) list.add(converter.convert(t, ContextTenantOptionRespDto.class));
        }
        return new ContextOptionsRespDto(u.platformAdmin(), list);
    }

    /**
     * 将当前平台管理员会话切换到 PLATFORM Context。
     *
     * @return 业务响应 DTO。
     */
    @Override
    public CurrentContextRespDto switchToPlatform() {
        var current = LoginUserUtils.getLoginUser();
        if (!current.platformAdmin())
            throw new BusinessException(CommonResultCode.FORBIDDEN, "普通用户不能进入 PLATFORM");
        UserEntity user = requireUser(current.userId());
        var next = loginContextAssembler.platform(user);
        save(next);
        return current();
    }

    /**
     * 校验 Tenant 可用性和访问资格后切换到指定 Tenant。
     *
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     * @return 业务响应 DTO。
     */
    @Override
    public CurrentContextRespDto switchToTenant(Long tenantId) {
        UserEntity user = requireUser(LoginUserUtils.getUserId());
        var next = loginContextAssembler.tenant(user, tenantId);
        save(next);
        return current();
    }

    /**
     * 保存当前登录 Context 并同步在线会话快照。
     *
     * @param user 已完成 Context 计算的登录用户快照
     */
    private void save(org.dromara.nova.common.security.model.CurrentLoginUser user) {
        LoginUserUtils.saveLoginUser(user);
        if (StpUtil.getTokenValue() != null) onlineUserService.activity(StpUtil.getTokenValue(), user);
        log.info("Context 切换完成 userId={} contextType={} tenantId={}", user.userId(), user.contextType(), user.tenantId());
    }

    /**
     * 加载有效用户，不存在时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private UserEntity requireUser(Long id) {
        UserEntity user = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.ID.eq(id)).and(USER_ENTITY.DELETED.eq(false)));
        if (user == null) throw new BusinessException(CommonResultCode.UNAUTHORIZED);
        return user;
    }

    /**
     * 判断 Tenant 是否处于可切换状态。
     *
     * @param t 待校验的 Tenant 实体
     * @return 业务校验或处理结果。
     */
    private boolean usable(TenantEntity t) {
        return t != null && !Boolean.TRUE.equals(t.getDeleted()) && Status.enabled(t.getStatus()) && (t.getExpireAt() == null || !t.getExpireAt().isBefore(LocalDate.now()));
    }
}
