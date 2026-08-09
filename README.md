# Nova Admin Backend Clean

Nova 企业级统一后台管理系统后端纯净实现。

本包仅包含 Backend 研发所需内容：

- `backend/`：Java 21 + Spring Boot 3.5.x Maven 多模块后端
- `sql/`：MySQL 8.4 空库初始化与基础数据
- `deploy/`：Docker Compose 本地基础设施
- `docs/`：API、数据库、权限、测试与交付说明
- `backend.md`：后端开发规范
- `BACKEND-EPICS.md`：后端重构/实现 Epic 顺序

不包含正式前端源码。

## 模块

```text
backend/
├── nova-common/
│   ├── nova-common-core
│   ├── nova-common-web
│   ├── nova-common-security
│   ├── nova-common-tenant
│   ├── nova-common-mybatis
│   ├── nova-common-cache
│   ├── nova-common-log
│   ├── nova-common-excel
│   └── nova-common-storage
├── nova-modules/
│   ├── nova-system
│   ├── nova-file
│   └── nova-message
└── nova-boot
```

## 构建

```bash
cd backend
mvn clean test
mvn clean package
java -jar nova-boot/target/nova-boot.jar
```

## 基础设施

```bash
cd deploy
docker compose up -d mysql redis minio
```

数据库首次启动自动执行 `sql/schema.sql` 和 `sql/data.sql`。

## 说明

源码按 `BACKEND-EPICS.md` 的完整闭环组织：SQL → Entity → Mapper/TableDef → DTO → MapStructPlus → Service →
Controller/OpenAPI → Permission → Tenant/DataScope → Cache/Audit → Test。
