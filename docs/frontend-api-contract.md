# Frontend Required Backend Contract

本文件记录本次 Backend 重实现直接覆盖的前端能力，便于后续 Codex 做 SoybeanAdmin 对接。

- ContextSwitcher：当前 Context、可选 Context、PLATFORM/TENANT 切换、Tenant 远程搜索。
- Header：当前用户 + 权限菜单 + 消息未读/最近 10 条/SSE。
- Dashboard：Tenant/Platform 真实统计。
- Profile：资料、头像、密码、只读 Context/Department/Role。
- User：查询/分页/CRUD/状态/密码/Role/导入/导出。
- Department：Tree CRUD/状态。
- Role：CRUD/DataScope/Menu Tree 全量授权。
- Menu：平台定义 Tree，Tenant 获取授权树与 Button Permission。
- Dictionary：左侧类型 + 右侧数据分页，类型/数据 CRUD。
- Config：分页/CRUD/敏感值脱敏。
- Monitor：登录日志、操作日志详情、在线用户、逻辑缓存。
- Operation Detail：Basic/Request/Response/Exception 四区单页所需完整字段。
- File：上传/列表/详情/预览/下载/删除。
- Message Admin：草稿、发送、撤回、接收人预览/阅读情况。
- Message Center：全部/未读/已读、点击自动已读、全部已读、未读真实数量、SSE。

Theme Config 的 Drawer/Modal 属于前端 UI Preference，不需要 Backend 配置表。
