package org.dromara.nova.common.mybatis.support;

import org.dromara.nova.common.mybatis.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * Service 写入实体时统一维护审计字段。
 */
public final class AuditEntitySupport {
    private AuditEntitySupport() {
    }

    /**
     * 初始化新建实体的审计字段，包括创建人、创建时间、修改人、修改时间、逻辑删除和版本。
     *
     * @param entity 业务实体
     * @param userId 用户 ID
     */
    public static void created(BaseEntity entity, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateBy(userId);
        entity.setCreateTime(now);
        entity.setUpdateBy(userId);
        entity.setUpdateTime(now);
        entity.setDeleted(false);
        entity.setVersion(0L);
    }

    /**
     * 刷新实体的修改人和修改时间审计字段。
     *
     * @param entity 业务实体
     * @param userId 用户 ID
     */
    public static void updated(BaseEntity entity, Long userId) {
        entity.setUpdateBy(userId);
        entity.setUpdateTime(LocalDateTime.now());
    }
}
