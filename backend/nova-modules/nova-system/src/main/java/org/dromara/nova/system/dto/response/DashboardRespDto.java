package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Tenant 工作台系统统计；公告由 message-center 接口独立获取，避免模块反向依赖。
 *
 * @param currentSpace     当前空间名称
 * @param userCount        用户数量
 * @param departmentCount  部门数量
 * @param roleCount        角色数量
 * @param onlineUserCount  当前在线用户数量
 * @param loginTrend       最近登录趋势数据
 * @param userStatus       用户状态分布数据
 * @param recentOperations 最近操作列表
 * @param department       当前部门名称
 */
@Schema(description = "Tenant 工作台聚合响应")
public record DashboardRespDto(@Schema(description = "当前空间名称") String currentSpace,
                               @Schema(description = "当前部门名称") String department,
                               @Schema(description = "用户数量") long userCount,
                               @Schema(description = "Department 数量") long departmentCount,
                               @Schema(description = "角色数量") long roleCount,
                               @Schema(description = "当前在线用户数量") long onlineUserCount,
/**
                                * 用户数量。
                                *
                                * @param date 日期
 * @param count 数量
                                */
                               @Schema(description = "最近登录趋势数据") List<TrendPoint> loginTrend,
                               @Schema(description = "用户状态分布数据") List<StatusPoint> userStatus,
                               @Schema(description = "最近操作列表") List<RecentOperation> recentOperations) {
    @Schema(description = "登录趋势数据点")
    public record TrendPoint(@Schema(description = "日期") String date, @Schema(description = "数量") long count) {
    }

    /**
     * 最近操作列表。
     *
     * @param name  名称
     * @param value 值
     */
    @Schema(description = "用户状态分布数据点")
    public record StatusPoint(@Schema(description = "名称") String name, @Schema(description = "值") long value) {
    }

    /**
     * 日期。
     *
     * @param operator    操作人
     * @param description 说明
     * @param time        时间文本
     */
    @Schema(description = "最近操作数据项")
    public record RecentOperation(@Schema(description = "操作人") String operator,
                                  @Schema(description = "说明") String description,
                                  @Schema(description = "时间文本") String time) {
    }
}
