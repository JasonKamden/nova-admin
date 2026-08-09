package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.MenuRespDto;

/**
 * 全局菜单、路由和按钮权限定义实体；Tenant 角色通过 sys_role_menu 进行授权。
 */

@AutoMapper(target = MenuRespDto.class, reverseConvertGenerate = false)
@Table("sys_menu")
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuEntity extends BaseEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 菜单类型：DIRECTORY、MENU 或 BUTTON。
     */
    @Column("menu_type")
    private String menuType;
    /**
     * 父节点 ID；根节点可为空。
     */
    @Column("parent_id")
    private Long parentId;
    /**
     * 菜单名称。
     */
    @Column("menu_name")
    private String menuName;
    /**
     * 前端路由名称。
     */
    @Column("route_name")
    private String routeName;
    /**
     * 前端路由路径。
     */
    @Column("route_path")
    private String routePath;
    /**
     * 前端组件路径。
     */
    @Column("component_path")
    private String componentPath;
    /**
     * 是否外链菜单。
     */
    @Column("external_link")
    private String externalLink;
    /**
     * 按钮或菜单权限编码。
     */
    @Column("permission_code")
    private String permissionCode;
    /**
     * 菜单图标。
     */
    @Column("icon")
    private String icon;
    /**
     * 前端国际化 Key。
     */
    @Column("i18n_key")
    private String i18nKey;
    /**
     * 排序值，数值越小越靠前。
     */
    @Column("sort")
    private Integer sort;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private Integer status;
    /**
     * 菜单是否可见。
     */
    @Column("visible")
    private Boolean visible;
    /**
     * 前端路由是否启用 KeepAlive。
     */
    @Column("keep_alive")
    private Boolean keepAlive;
}
