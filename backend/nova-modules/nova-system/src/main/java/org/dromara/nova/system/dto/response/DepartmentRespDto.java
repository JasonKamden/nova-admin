package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Department Tree 节点。
 */
@Data
@Schema(description = "Department 树节点响应")
public class DepartmentRespDto {
    @Schema(description = "子节点集合")
    private final List<DepartmentRespDto> children = new ArrayList<>();
    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "父节点 ID；根节点可为空")
    private Long parentId;
    @Schema(description = "Department 编码，Tenant 内唯一")
    private String departmentCode;
    @Schema(description = "Department 名称")
    private String departmentName;
    @Schema(description = "Department 负责人用户 ID")
    private Long leaderUserId;
    @Schema(description = "Department 负责人名称")
    private String leaderName;
    @Schema(description = "联系电话或手机号")
    private String phone;
    @Schema(description = "邮箱地址")
    private String email;
    @Schema(description = "排序值，数值越小越靠前")
    private Integer sort;
    @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准")
    private Integer status;
}
