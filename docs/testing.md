# Testing

测试策略：

1. Common：树、日志脱敏、Storage 路径安全、缓存注册等纯单元测试。
2. System：Auth/Context/Tenant、Department、UserRole/RoleMenu、DataScope Service 单元测试。
3. File：LocalStorage 与文件访问边界测试。
4. Message：富文本清洗、DRAFT/SENT/WITHDRAWN 状态、已读幂等、接收快照测试。
5. Testcontainers：MySQL 8.4 空库脚本、Mapper、Tenant Fence 与关系更新。
6. Smoke：Boot 启动后按 Epic 顺序真实调用接口。

正式研发环境执行：

```bash
cd backend
mvn clean compile
mvn test
mvn package
mvn dependency:tree
```

还必须确认 `target/generated-sources/annotations` 中 MyBatis-Flex TableDef 和 MapStructPlus 生成代码正常。
