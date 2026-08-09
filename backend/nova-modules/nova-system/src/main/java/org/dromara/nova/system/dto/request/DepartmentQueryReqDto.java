package org.dromara.nova.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 部门树查询。
 *
 * @param keyword 模糊搜索关键字
 * @param status  状态：通常 1 启用、0 停用，具体以业务枚举为准
 */
@Schema(description = "Department 树查询参数")
public record DepartmentQueryReqDto(@Schema(description = "模糊搜索关键字") String keyword,
                                    @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") Integer status) {
}
