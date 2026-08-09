package org.dromara.nova.system.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 在 Docker 可用时验证根目录 SQL 可以初始化空 MySQL 8.4。
 */
@Testcontainers(disabledWithoutDocker = true)
class SchemaInitializationTest {
    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("nova_admin")
            .withUsername("nova")
            .withPassword("nova-test-password");

    @Test
    void schemaCanInitializeAnEmptyDatabase() throws Exception {
        Path root = locateRepositoryRoot();
        executeScript(root.resolve("sql/schema.sql"));
        executeScript(root.resolve("sql/data.sql"));
        try (var connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var statement = connection.createStatement();
             var result = statement.executeQuery("select count(*) from information_schema.tables where table_schema='nova_admin' and table_name='sys_user'")) {
            result.next();
            assertEquals(1, result.getInt(1));
        }
    }

    /**
     * 执行 executeScript 业务处理。
     *
     * @param path path 参数
     */
    private void executeScript(Path path) throws Exception {
        String sql = Files.readString(path);
        try (var connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var statement = connection.createStatement()) {
            for (String part : sql.split(";")) {
                String command = part.trim();
                if (!command.isBlank() && !command.startsWith("--")) statement.execute(command);
                else if (command.startsWith("--")) {
                    String withoutComments = command.lines().filter(line -> !line.stripLeading().startsWith("--")).reduce("", (a, b) -> a + "\n" + b).trim();
                    if (!withoutComments.isBlank()) statement.execute(withoutComments);
                }
            }
        }
    }

    /**
     * 执行 locateRepositoryRoot 业务处理。
     *
     * @return 方法处理结果。
     */
    private Path locateRepositoryRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null) {
            if (Files.exists(current.resolve("sql/schema.sql"))) return current;
            current = current.getParent();
        }
        throw new IllegalStateException("Unable to locate sql/schema.sql from user.dir");
    }
}
