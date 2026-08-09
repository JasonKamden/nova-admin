package org.dromara.nova.system.service;

import org.dromara.nova.common.security.model.CurrentLoginUser;
import org.dromara.nova.system.dto.request.LoginReqDto;
import org.dromara.nova.system.dto.response.CaptchaRespDto;
import org.dromara.nova.system.dto.response.LoginRespDto;
import org.dromara.nova.system.dto.response.MenuRespDto;

import java.util.List;

/**
 * 登录认证、当前用户菜单和权限查询业务契约。
 */
public interface AuthService {
    /**
     * 生成图形验证码。
     */
    CaptchaRespDto captcha();

    /**
     * 校验账号密码并创建登录会话。
     */
    LoginRespDto login(LoginReqDto request);

    /**
     * 注销当前登录会话。
     */
    void logout();

    /**
     * 返回当前登录用户快照。
     */
    CurrentLoginUser currentUser();

    /**
     * 返回当前 Context 下的动态菜单。
     */
    List<MenuRespDto> currentMenus();

    /**
     * 返回当前 Context 下的按钮权限编码。
     */
    List<String> currentPermissions();
}
