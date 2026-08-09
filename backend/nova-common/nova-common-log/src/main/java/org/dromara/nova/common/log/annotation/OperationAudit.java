package org.dromara.nova.common.log.annotation;

import java.lang.annotation.*;

/**
 * 标记需要持久化审计的核心操作。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationAudit {
    /**
     * 业务模块稳定编码，例如 USER、ROLE、MESSAGE。
     */
    String module();

    /**
     * 操作类型稳定编码，例如 CREATE、UPDATE、DELETE、SEND。
     */
    String type();

    /**
     * 面向审计人员的业务操作说明，不承载程序判断逻辑。
     */
    String description();
}
