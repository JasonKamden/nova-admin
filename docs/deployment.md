# Deployment

## 本地 Maven

要求 Java 21、Maven 3.9.16、MySQL 8.4、Redis、可选 MinIO。

```bash
cd backend
mvn -ntp clean compile
mvn -ntp test
mvn -ntp package
java -jar nova-boot/target/nova-boot.jar
```

## Docker Compose

先创建本地 Secret 文件：

```bash
cd deploy
cp .env.example .env
```

必须修改 `.env` 中的 MySQL、Redis、MinIO 凭据，然后执行：

```bash
docker compose up -d --build
```

Compose 会使用 `deploy/backend/Dockerfile` 构建 Backend，并启动 MySQL 8.4、Redis、MinIO 和 Nova Backend。

主要运行变量：`SERVER_PORT`、`MYSQL_HOST/PORT/DATABASE/USERNAME/PASSWORD`、`REDIS_HOST/PORT/PASSWORD`、`STORAGE_TYPE`、
`STORAGE_LOCAL_DIR`、`MINIO_ENDPOINT/ACCESS_KEY/SECRET_KEY/BUCKET`、`VIRTUAL_THREADS_ENABLED`。

`application-prod.yml` 不为数据库/Redis/MinIO Secret 提供可工作的生产默认值。生产环境应通过 Secret Manager、容器 Secret
或部署环境注入凭据。
