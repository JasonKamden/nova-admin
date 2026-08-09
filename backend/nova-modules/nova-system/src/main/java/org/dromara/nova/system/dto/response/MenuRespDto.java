package org.dromara.nova.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树节点。
 */
@Schema(description = "菜单树节点与动态路由响应")
public class MenuRespDto {
    @Schema(description = "主键 ID")
    public Long id;
    @Schema(description = "菜单类型：DIRECTORY、MENU 或 BUTTON")
    public String menuType;
    @Schema(description = "父节点 ID；根节点可为空")
    public Long parentId;
    @Schema(description = "菜单名称")
    public String menuName;
    @Schema(description = "前端路由名称")
    public String routeName;
    @Schema(description = "前端路由路径")
    public String routePath;
    @Schema(description = "前端组件路径")
    public String componentPath;
    @Schema(description = "是否外链菜单")
    public String externalLink;
    @Schema(description = "按钮或菜单权限编码")
    public String permissionCode;
    @Schema(description = "菜单图标")
    public String icon;
    @Schema(description = "前端国际化 Key")
    public String i18nKey;
    @Schema(description = "排序值，数值越小越靠前")
    public Integer sort;
    @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准")
    public Integer status;
    @Schema(description = "菜单是否可见")
    public Boolean visible;
    @Schema(description = "前端路由是否启用 KeepAlive")
    public Boolean keepAlive;
    @Schema(description = "子节点集合")
    public List<MenuRespDto> children = new ArrayList<>();
}
