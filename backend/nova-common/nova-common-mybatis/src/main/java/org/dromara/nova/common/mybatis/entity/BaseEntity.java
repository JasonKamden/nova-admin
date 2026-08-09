package org.dromara.nova.common.mybatis.entity;

import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 可变业务实体通用审计字段。
 */
@Data
public abstract class BaseEntity {
    /**
     * 创建人用户 ID。
     */
    @Column("create_by")
    private Long createBy;
    /**
     * 创建时间。
     */
    @Column("create_time")
    private LocalDateTime createTime;
    /**
     * 最后更新人用户 ID。
     */
    @Column("update_by")
    private Long updateBy;
    /**
     * 最后更新时间。
     */
    @Column("update_time")
    private LocalDateTime updateTime;
    /**
     * 逻辑删除标识。
     */
    @Column("deleted")
    private Boolean deleted;
    /**
     * 乐观锁版本号。
     */
    @Column("version")
    private Long version;
}
