package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.nova.system.dto.response.LoginLogRespDto;

import java.time.LocalDateTime;

/**
 * 登录审计日志实体，记录成功/失败结果、IP、User-Agent 和失败原因。
 */

@AutoMapper(target = LoginLogRespDto.class, reverseConvertGenerate = false)
@Table("sys_login_log")
@Data
public class LoginLogEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 用户 ID。
     */
    @Column("user_id")
    private Long userId;
    /**
     * 登录账号。
     */
    @Column("username")
    private String username;
    /**
     * 当前运行上下文类型：PLATFORM 或 TENANT。
     */
    @Column("context_type")
    private String contextType;
    /**
     * Tenant ID；Tenant 业务写入以服务端可信 Context 为准。
     */
    @Column("tenant_id")
    private Long tenantId;
    /**
     * 部门ID。
     */
    @Column("department_id")
    private Long departmentId;
    /**
     * 登录类型。
     */
    @Column("login_type")
    private String loginType;
    /**
     * 登录结果状态。
     */
    @Column("login_status")
    private Integer loginStatus;
    /**
     * 客户端 IP 地址。
     */
    @Column("ip")
    private String ip;
    /**
     * 客户端 User-Agent。
     */
    @Column("user_agent")
    private String userAgent;
    /**
     * 登录时间。
     */
    @Column("login_time")
    private LocalDateTime loginTime;
    /**
     * 失败原因。
     */
    @Column("failure_reason")
    private String failureReason;
    /**
     * 请求唯一标识。
     */
    @Column("request_id")
    private String requestId;
}
