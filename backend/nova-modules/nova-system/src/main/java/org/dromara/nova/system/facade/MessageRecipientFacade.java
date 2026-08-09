package org.dromara.nova.system.facade;

import java.util.List;

/**
 * Message 模块使用的系统用户收件人只读 Facade，禁止跨模块访问 Mapper。
 */
public interface MessageRecipientFacade {
    List<RecipientUser> all();

    List<RecipientUser> departments(List<Long> departmentIds, boolean includeChildren);

    List<RecipientUser> roles(List<Long> roleIds);

    List<RecipientUser> users(List<Long> userIds);

    /**
     * 消息接收人快照。
     *
     * @param userId             用户 ID
     * @param departmentId       部门ID
     * @param username           登录账号
     * @param nickname           用户昵称或姓名
     * @param departmentName部门名称
     */
    record RecipientUser(Long userId, Long departmentId, String username, String nickname, String departmentName) {
    }
}
