package org.dromara.nova.system.dto.request;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dromara.nova.system.entity.MenuEntity;
import org.dromara.nova.system.enums.MenuType;

/**
 * 菜单新增请求。
 *
 * @param menuType       菜单类型：DIRECTORY、MENU 或 BUTTON
 * @param parentId       父节点 ID；根节点可为空
 * @param menuName       菜单名称
 * @param routeName      前端路由名称
 * @param routePath      前端路由路径
 * @param componentPath  前端组件路径
 * @param externalLink   是否外链菜单
 * @param permissionCode 按钮或菜单权限编码
 * @param icon           菜单图标
 * @param i18nKey        前端国际化 Key
 * @param sort           排序值，数值越小越靠前
 * @param status         状态：通常 1 启用、0 停用，具体以业务枚举为准
 * @param visible        菜单是否可见
 * @param keepAlive      前端路由是否启用 KeepAlive
 */
@AutoMapper(target = MenuEntity.class, reverseConvertGenerate = false)
@Schema(description = "新增全局菜单请求参数")
public record MenuCreateReqDto(@Schema(description = "菜单类型：DIRECTORY、MENU 或 BUTTON") @NotNull MenuType menuType,
                               @Schema(description = "父节点 ID；根节点可为空") Long parentId,
                               @Schema(description = "菜单名称") @NotBlank String menuName,
                               @Schema(description = "前端路由名称") String routeName,
                               @Schema(description = "前端路由路径") String routePath,
                               @Schema(description = "前端组件路径") String componentPath,
                               @Schema(description = "是否外链菜单") String externalLink,
                               @Schema(description = "按钮或菜单权限编码") String permissionCode,
                               @Schema(description = "菜单图标") String icon,
                               @Schema(description = "前端国际化 Key") String i18nKey,
                               @Schema(description = "排序值，数值越小越靠前") Integer sort,
                               @Schema(description = "状态：通常 1 启用、0 停用，具体以业务枚举为准") @NotNull Integer status,
                               @Schema(description = "菜单是否可见") Boolean visible,
                               @Schema(description = "前端路由是否启用 KeepAlive") Boolean keepAlive) {
}
