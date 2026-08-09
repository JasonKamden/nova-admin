package org.dromara.nova.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.nova.common.mybatis.entity.BaseEntity;
import org.dromara.nova.system.dto.response.UserRespDto;

import java.time.LocalDateTime;

/**
 * 平台级用户身份实体；Tenant 归属通过 sys_user_tenant 维护，不直接固化到用户表。
 */

@AutoMapper(target = UserRespDto.class, reverseConvertGenerate = false)
@Table("sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends BaseEntity {
    /**
     * 主键 ID。
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    /**
     * 登录账号。
     */
    @Column("username")
    private String username;
    /**
     * 登录密码，仅用于认证请求，不会回显。
     */
    @Column("password")
    private String password;
    /**
     * 用户昵称或姓名。
     */
    @Column("nickname")
    private String nickname;
    /**
     * 用户头像地址或文件标识。
     */
    @Column("avatar")
    private String avatar;
    /**
     * 性别编码。
     */
    @Column("gender")
    private String gender;
    /**
     * 联系电话或手机号。
     */
    @Column("phone")
    private String phone;
    /**
     * 邮箱地址。
     */
    @Column("email")
    private String email;
    /**
     * 个人简介。
     */
    @Column("bio")
    private String bio;
    /**
     * 是否平台管理员。
     */
    @Column("platform_admin")
    private Boolean platformAdmin;
    /**
     * 状态：通常 1 启用、0 停用，具体以业务枚举为准。
     */
    @Column("status")
    private Integer status;
    /**
     * 最近一次登录时间。
     */
    @Column("last_login_time")
    private LocalDateTime lastLoginTime;
    /**
     * 最近一次登录 IP。
     */
    @Column("last_login_ip")
    private String lastLoginIp;
}
