package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.nova.system.dto.response.OperationLogRespDto;

import java.time.LocalDateTime;

/**
 * 核心业务操作审计实体，分字段保存脱敏请求、响应、异常、耗时和链路标识。
 */

@AutoMapper(target = OperationLogRespDto.class, reverseConvertGenerate = false)
@Table("sys_operation_log")
@Data
public class OperationLogEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 操作所属业务模块。
     */
    @Column("module")
    private String module;
    /**
     * 操作类型。
     */
    @Column("operation_type")
    private String operationType;
    /**
     * 操作描述。
     */
    @Column("operation_description")
    private String operationDescription;
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
     * HTTP 请求方法。
     */
    @Column("request_method")
    private String requestMethod;
    /**
     * 请求 URI。
     */
    @Column("request_uri")
    private String requestUri;
    /**
     * 请求客户端 IP。
     */
    @Column("request_ip")
    private String requestIp;
    /**
     * 客户端 User-Agent。
     */
    @Column("user_agent")
    private String userAgent;
    /**
     * 文件或请求的 MIME 类型。
     */
    @Column("content_type")
    private String contentType;
    /**
     * 脱敏后的白名单请求头。
     */
    @Column("request_headers")
    private String requestHeaders;
    /**
     * 请求 Query 参数。
     */
    @Column("query_params")
    private String queryParams;
    /**
     * 请求路径参数。
     */
    @Column("path_params")
    private String pathParams;
    /**
     * 脱敏后的请求体。
     */
    @Column("request_body")
    private String requestBody;
    /**
     * HTTP 响应状态码。
     */
    @Column("http_status")
    private Integer httpStatus;
    /**
     * 业务响应码。
     */
    @Column("business_code")
    private Integer businessCode;
    /**
     * 操作结果状态。
     */
    @Column("operation_status")
    private String operationStatus;
    /**
     * 脱敏后的响应体。
     */
    @Column("response_body")
    private String responseBody;
    /**
     * 异常 Java 类型。
     */
    @Column("exception_type")
    private String exceptionType;
    /**
     * 稳定业务错误码。
     */
    @Column("error_code")
    private String errorCode;
    /**
     * 异常或导入错误信息。
     */
    @Column("error_message")
    private String errorMessage;
    /**
     * 异常首个堆栈位置。
     */
    @Column("exception_location")
    private String exceptionLocation;
    /**
     * 完整异常堆栈（已按日志策略截断）。
     */
    @Column("exception_stack")
    private String exceptionStack;
    /**
     * 执行耗时，单位毫秒。
     */
    @Column("duration_ms")
    private Long durationMs;
    /**
     * 请求唯一标识。
     */
    @Column("request_id")
    private String requestId;
    /**
     * 链路追踪标识。
     */
    @Column("trace_id")
    private String traceId;
    /**
     * 操作发生时间。
     */
    @Column("operation_time")
    private LocalDateTime operationTime;
}
