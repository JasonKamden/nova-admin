package org.dromara.nova.message.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageContentSanitizerTest {
    @Test
    void removesExecutableHtmlButKeepsBusinessFormatting() {
        MessageContentSanitizer sanitizer = new MessageContentSanitizer();
        String cleaned = sanitizer.clean("<p onclick=\"alert(1)\">通知<strong>正文</strong><script>alert(2)</script><a href=\"javascript:alert(3)\">link</a></p>");
        assertTrue(cleaned.contains("<strong>正文</strong>"));
        assertFalse(cleaned.contains("onclick"));
        assertFalse(cleaned.contains("<script"));
        assertFalse(cleaned.contains("javascript:"));
    }
}
