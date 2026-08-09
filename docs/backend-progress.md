# Backend Progress

## Epic 源码覆盖

当前纯净包已建立 BE-00 ~ BE-09 的主干实现，并提供 BE-10 的静态验证脚本、Testcontainers SQL 初始化测试和 Docker 部署文件。

- BE-00：统一工程/响应/异常、MyBatis-Flex APT、MapStructPlus、Sa-Token、Tenant Context、缓存/日志/Storage 基础。
- BE-01：Auth、Context、Tenant 生命周期与初始化。
- BE-02：Department/User/Role/Menu/RBAC/DataScope。
- BE-03：Dictionary/Config。
- BE-04：LoginLog/OperationLog/OnlineUser。
- BE-05：Logical Cache Management。
- BE-06：Profile/Dashboard。
- BE-07：File/Local/MinIO/业务引用。
- BE-08：Message/Message Center/SSE。
- BE-09：FastExcel 用户导入导出。

## 当前已执行静态验证

- 所有 Maven POM XML 可解析。
- Maven 聚合模块路径存在。
- Java public top-level 类型与文件名检查。
- `org.dromara.nova` 内部源码 import 存在性检查（APT 生成 TableDef 除外）。
- 无 Mapper XML。
- 无 BeanUtils/BeanCopier。
- 无普通 Mapper `@Select/@Update/@Insert/@Delete` SQL。
- 无 `NOVA_` 环境变量前缀。
- 无 `.editorconfig`、`target`、Jar/Class/日志等交付污染。
- SQL 核心表与禁止表检查。
- `javac -proc:none` 进行语法解析级辅助检查；因生成容器没有 Maven 依赖 classpath，外部依赖类型错误不作为构建通过证明。

## 本轮源码备注与日志完善

已完成源码级注释与接口契约补强：

- 20 个 Controller 全部补齐 Springdoc `@Tag`。
- 104 个 HTTP Endpoint 全部补齐 `@Operation`。
- Path/Query/File 参数补齐 `@Parameter`，查询 DTO 使用 `@ParameterObject`。
- 67 个 Request/Response DTO 全部补齐 `@Schema`，敏感密码字段标记为 WriteOnly。
- 20 个 Entity 共 206 个字段全部补齐 JavaDoc。
- 20 个 Service 接口共 117 个方法全部补齐职责说明。
- 核心写操作通过 `@OperationAudit` 统一记录结构化运行日志和持久化审计，补齐 Dictionary/Config/Cache/OnlineUser/Department
  Status 等审计缺口。
- Auth 补充成功/失败结构化日志，Context 切换补充目标上下文日志，日志不输出密码、Token、Cookie、Secret 等敏感值。

详细说明见 `docs/source-comments-and-logging.md`。

## 尚需真实运行验证

当前生成容器未安装 Maven/Docker，且不能下载外部 Maven 依赖，因此尚未在本环境完成：

```text
mvn clean compile
mvn test
mvn package
APT generated-sources 验证
MySQL 8.4 Testcontainers/空库初始化
NovaApplication 启动
全 API Smoke Test
Redis/MinIO/SSE 多节点联调
```

这些步骤是 BE-10 的强制验收项。运行时验证通过前，本包定位为“完整 Backend 纯净源码实现基线”，不是“生产验收通过版本”。
