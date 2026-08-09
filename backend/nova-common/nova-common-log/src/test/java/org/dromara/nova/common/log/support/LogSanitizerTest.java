package org.dromara.nova.common.log.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogSanitizerTest {
    @Test
    void masksNestedSensitiveFieldsAndTruncatesLongText() {
        LogSanitizer sanitizer = new LogSanitizer(new ObjectMapper(), 80);
        String json = sanitizer.json(Map.of("username", "kamden", "password", "secret", "nested", Map.of("accessToken", "token-value")));
        assertFalse(json.contains("secret"));
        assertFalse(json.contains("token-value"));
        assertTrue(json.contains("******"));
        assertTrue(sanitizer.truncate("x".repeat(100)).contains("TRUNCATED"));
    }
}
