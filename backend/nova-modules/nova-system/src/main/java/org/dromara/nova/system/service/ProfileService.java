package org.dromara.nova.system.service;

import org.dromara.nova.system.dto.request.ProfilePasswordReqDto;
import org.dromara.nova.system.dto.request.ProfileUpdateReqDto;
import org.dromara.nova.system.dto.response.ProfileRespDto;

/**
 * 当前登录用户个人资料、密码和头像维护业务契约。
 */
public interface ProfileService {
    /**
     * 查询个人中心聚合信息。
     */
    ProfileRespDto get();

    /**
     * 修改业务数据并执行状态、权限和 Tenant 校验。
     */
    ProfileRespDto update(ProfileUpdateReqDto request);

    /**
     * 校验旧密码并修改密码，成功后使当前会话失效。
     */
    void changePassword(ProfilePasswordReqDto request);

    /**
     * 更新头像并返回最新个人信息。
     */
    String updateAvatar(org.springframework.web.multipart.MultipartFile file);

    /**
     * 校验当前用户头像访问权限并返回头像文件流。
     */
    org.dromara.nova.system.model.ProfileAvatar openAvatar();
}
