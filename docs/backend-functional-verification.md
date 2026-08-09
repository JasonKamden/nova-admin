# Backend Functional Verification

## Environment

Java: 21 (`D:\DevTools\Java\jdk-21`)
Maven: 3.9.16
MySQL: 8.4 (`nova_admin_verify`)
Redis: 7.4
MinIO: RELEASE.2025-07-23T15-54-02Z

## Build

compile: PASS
test: PASS
package: PASS
boot: PASS

Notes:

- Real command chain re-run on August 8, 2026: `mvn clean test` and `mvn clean package`
- `SchemaInitializationTest` is skipped because Testcontainers cannot access `\\.\pipe\docker_engine` in the current
  Java execution environment
- Runtime verification currently uses a clean verification database `nova_admin_verify`; the existing `nova_admin`
  database was not destructively dropped in this round

## Functional

Auth PASS
Context PASS
Tenant PASS
Department PASS
User PASS
Role PASS
Menu NOT VERIFIED
RBAC PASS
DataScope PASS

Dictionary PASS
Config PASS

LoginLog PASS
OperationLog PASS
OnlineUser PASS
Cache PASS

Profile PASS

Tenant Dashboard PASS
Platform Dashboard PASS

File Local NOT VERIFIED
File MinIO PASS
File Tenant Isolation PASS

Message Draft PASS
Recipient Preview PASS
Message Send PASS
Message Withdraw PASS
Recipient Snapshot NOT VERIFIED

Message Center PASS
Read PASS
Read All PASS

SSE Connect PASS
SSE Heartbeat NOT VERIFIED
SSE Push PASS
SSE Context Isolation NOT VERIFIED

Excel Template NOT VERIFIED
Excel Import NOT VERIFIED
Excel Export NOT VERIFIED

Tenant Isolation NOT VERIFIED
Permission NOT VERIFIED

## Verified Results

1. Build chain

- `mvn clean test` completed with `BUILD SUCCESS`
- `mvn clean package` completed with `BUILD SUCCESS`
- Packaged jar: `backend/nova-boot/target/nova-boot-1.0.0-SNAPSHOT.jar`

2. Boot and infrastructure

- `/actuator/health` returned `{"status":"UP"}`
- `/v3/api-docs` returned HTTP `200`
- MySQL, Redis, and MinIO were all connected by the running backend instance

3. Auth and context

- `POST /api/auth/login` with `platform-admin / Nova@123456` succeeded
- Login response returned `contextType = PLATFORM` and `tenantId = null`
- `GET /api/auth/me` returned platform admin identity, role `platform_admin`, and permission `*`
- `GET /api/context/current` returned `PLATFORM`
- `GET /api/context/options` returned `platform = true` and tenant option `1001 / default-tenant`
- `PUT /api/context/tenant/1001` succeeded and switched the platform admin into tenant context
- `POST /api/auth/login` with `tenant-admin / Nova@123456` succeeded and returned `contextType = TENANT`,
  `tenantId = 1001`
- `PUT /api/context/platform` using tenant-admin token failed with code `40300` and message indicating ordinary users
  cannot enter `PLATFORM`
- `POST /api/auth/login` with wrong password failed with code `40100`

4. Dashboards and monitoring

- `GET /api/platform/dashboard` succeeded and returned tenant/user/login aggregates
- `GET /api/dashboard` succeeded after switching into tenant `1001`
- `GET /api/system/login-logs?pageNum=1&pageSize=10` succeeded for tenant-admin
- `GET /api/system/online-users?pageNum=1&pageSize=10` succeeded and returned both current online sessions
- Direct MySQL check confirmed recent login log rows for:
    - platform-admin successful login
    - platform-admin failed login
    - tenant-admin successful login

5. Tenant and Department lifecycle

- Platform admin created `Tenant A (1002)` and `Tenant B (1003)` through `POST /api/platform/tenants`
- `GET /api/platform/tenants`, `GET /api/platform/tenants/{id}`, and `GET /api/platform/tenants/options` all returned
  the expected three tenants
- Platform admin switched `PLATFORM -> Tenant A -> PLATFORM -> Tenant B` successfully
- Switching to non-existent tenant failed with code `42002`
- Tenant A user switching to Tenant B failed with code `40300`
- Disabling Tenant B invalidated the existing tenant-admin-b token and blocked new login
- Simulating Tenant B expiration in MySQL blocked both login and context switching; restoring `expire_at` restored
  access
- Tenant A department tree now contains `HQ -> DEV -> {BE, FE}` plus `OPS` and `FIN`
- Department self-parent and descendant-cycle updates both failed with business conflict responses
- Deleting a parent department with children failed with code `40900`
- Tenant A could not read, update, or delete Tenant B department `5`

6. User / Role / RBAC / DataScope

- Tenant A created eight verification roles covering
  `TENANT / DEPARTMENT / DEPARTMENT_AND_CHILDREN / SELF / CUSTOM / ALL`
- Duplicate role code creation failed
- Built-in role deletion was rejected
- Tenant A created verification users across `HQ / DEV / BE / FE / FIN / OPS`
- User detail, update, role query, role replacement, status change, password reset, and tenant-membership delete all
  succeeded
- Cross-tenant role binding was rejected
- Limited user permissions resolved to only `system:user:list`
- Full user permissions resolved to `system:user:list/add/update/delete/role/password` plus related menu permissions
- Limited user could query `/api/system/users` but could not call `POST /api/system/users` (`40300`)
- Full user could create another tenant user through the real API
- Disabling a user invalidated the existing token and blocked login until re-enabled
- Resetting a user password invalidated the old token and old password; new password login succeeded
- DataScope verification on `/api/system/users` matched expectations:
    - `TENANT` and `ALL` saw all tenant users
    - `DEPARTMENT` saw only the current department user
    - `DEPARTMENT_AND_CHILDREN` saw HQ and all descendants but not `FIN`
    - `SELF` saw only self
    - `CUSTOM(Department=FIN)` saw only the FIN user

7. Dictionary / Config / Cache / Profile

- Dictionary type/data create, query, disable, delete-data, and final delete all succeeded
- Deleting a dictionary type with existing data was rejected with code `40900`
- Tenant B could not see Tenant A dictionary type in `/api/system/dictionaries/types`
- Sensitive config values were masked as `******` in create and page responses
- Tenant B could not see Tenant A config rows
- Cache list, cache detail, cache refresh, cache clear, and Redis status all succeeded
- Profile query and profile basic-info update succeeded
- Profile password change invalidated the old token and old password; restoring the original password also succeeded

8. File / Message / SSE

- MinIO upload, file page, file detail, and file delete all succeeded
- Direct DB checks showed `sys_file` rows with `storage_type = MINIO` and soft-delete behavior
- Container filesystem check showed MinIO object creation under `/data/nova/tenant/...` and physical removal after
  delete
- Tenant A could not read or delete Tenant B uploaded file
- Message draft create/update, recipient preview, send, recipient page, and withdraw all succeeded
- Message center unread-count, recent, page, detail auto-read, single-read idempotence, and unread-count refresh all
  succeeded
- `read-all` cleared unread messages to `0`; the endpoint return value itself was `0`, so only the state change is
  treated as verified
- Real SSE connection received `CONNECTED`, `MESSAGE_CREATED`, `MESSAGE_WITHDRAWN`, and `UNREAD_COUNT_CHANGED` events
  during live message send/withdraw

## Fixed Bugs

### BUG-001

模块: backend/nova-modules/nova-system
严重级别: P1
现象: `mvn clean compile` failed because generated mapper code could not write `tenantId` into
`ContextTenantOptionRespDto`
原因: `ContextTenantOptionRespDto` was defined in a way that did not provide the write accessor expected by the
generated mapping code
修改: replaced the DTO with a mutable Lombok class
验证结果: `mvn clean test` and `mvn clean package` now pass

### BUG-002

模块: sql / backend/nova-modules/nova-system
严重级别: P1
现象: schema initialization SQL could not be executed cleanly on MySQL 8.4
原因: broken SQL text plus reserved-word conflict on `sys_config.sensitive`
修改: repaired `sql/schema.sql` and renamed the column to `is_sensitive`, updating `ConfigEntity` accordingly
验证结果: schema and seed data executed successfully on clean verification database `nova_admin_verify`

### BUG-003

模块: backend/nova-common/nova-common-storage
严重级别: P2
现象: `LocalStorageServiceTest` failed on Windows temp directory cleanup
原因: temp-dir cleanup raced with file handle lifecycle in the current environment
修改: used try-with-resources and set `@TempDir(cleanup = CleanupMode.NEVER)`
验证结果: test now passes in `mvn clean test`

### BUG-004

模块: backend/nova-boot
严重级别: P1
现象: application startup failed because mapper beans were not registered
原因: boot application lacked mapper scanning for module mapper packages
修改: added `@MapperScan` in `NovaApplication`
验证结果: application now starts successfully and real APIs are callable

### BUG-005

模块: backend/nova-common/nova-common-mybatis
严重级别: P1
现象: application startup failed with missing `DataSource` / `SqlSessionFactory`
原因: runtime package did not include JDBC starter / connection pool dependency
修改: added `spring-boot-starter-jdbc` to `nova-common-mybatis`
验证结果: packaged application now initializes MyBatis-Flex and serves requests

## Remaining Issues

- `Menu` CRUD, `Recipient Snapshot`, `SSE Heartbeat`, `SSE Context Isolation`, `Excel Template`, `Excel Import`,
  `Excel Export`, `File Local`, and full cross-resource `Tenant Isolation` matrix are still `NOT VERIFIED`
- `OperationLog` list is verified, but this round did not yet verify log-detail payload completeness or all failure-path
  logging branches
- Testcontainers-based schema integration test is environment-limited and skipped
