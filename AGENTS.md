# AGENTS.md

## 1. 作用

本文件约束 Codex/AI Agent 在本后端纯净源码包中的执行方式。详细业务和技术规范以 `backend.md` 为准，实施顺序以
`BACKEND-EPICS.md` 为准。

规则优先级：用户最新明确要求 > 当前可构建真实源码 > `AGENTS.md` > `backend.md` > `docs/` > 通用经验。

## 2. 开发边界

- 只维护 `backend/`、`sql/`、`docs/`、`deploy/`。
- 不创建 Organization、Post、Liquibase、Mapper XML。
- PLATFORM 是 Context，不是 Tenant，禁止 `tenantId=0/-1`。
- 部门使用完整命名，禁止正式领域名 `Dept/dept`。
- `nova-boot -> nova-modules -> nova-common`，common 不承载业务。
- 模块间不得直接调用对方 Controller/Mapper；跨模块能力使用明确 Service/Facade 契约。

## 3. 代码要求

- Java 21、Spring Boot 3.5.x、MyBatis-Flex、MapStructPlus、Sa-Token。
- 普通 CRUD/Query 使用 MyBatis-Flex QueryWrapper/TableDef；特殊 SQL 才允许注解 SQL，并说明原因。
- 普通 Entity/DTO 映射优先 MapStructPlus；禁止 BeanUtils、BeanCopier、反射复制和 JSON 复制对象。
- Controller 不返回 Entity；统一 `R<T>` / `PageResult<T>`。
- Tenant 业务写入从可信 `TenantContext` 获取 tenantId，不信任客户端 tenantId。
- 权限必须后端再次校验；前端隐藏按钮不构成安全边界。
- 日志禁止记录密码、Token、Cookie、Secret、AccessKey/SecretKey 和文件二进制。
- 禁止 TODO、空方法、Mock 业务数据、未实现占位类。

## 4. 每个 Epic 的完成门槛

每个功能必须形成：

`SQL -> Entity -> Mapper/TableDef -> DTO -> Service -> Controller/OpenAPI -> Permission -> Tenant/DataScope -> Cache/Audit -> Test -> Build/Start`。

执行环境可用时必须真实运行：

```bash
cd backend
mvn clean compile
mvn test
mvn package
```

并检查 `target/generated-sources/annotations` 中 MyBatis-Flex TableDef 与 MapStructPlus
生成结果。构建或启动失败必须修复后再继续，不得把“文件已创建”写成“功能已完成”。
