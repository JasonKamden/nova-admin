package org.dromara.nova.common.log.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 日志敏感字段脱敏与长度限制。
 *
 * <p>审计日志允许记录业务请求/响应，但必须先基于字段名递归脱敏；任何无法安全序列化的对象
 * 都不会回退到 {@code toString()}，避免 Record/DTO 的默认字符串把密码、Token 等敏感值写入日志。</p>
 */
public class LogSanitizer {
    private static final Set<String> SENSITIVE_EXACT = Set.of(
            "authorization", "cookie", "set-cookie", "accesskey", "apikey", "credential"
    );

    private final ObjectMapper objectMapper;
    private final int maxLength;

    public LogSanitizer(ObjectMapper objectMapper, int maxLength) {
        this.objectMapper = objectMapper;
        this.maxLength = maxLength;
    }

    /**
     * 将任意可序列化对象转换为已经脱敏和截断的 JSON 文本。
     */
    public String json(Object value) {
        if (value == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.valueToTree(value);
            sanitize(node);
            return truncate(objectMapper.writeValueAsString(node));
        } catch (Exception exception) {
            return "[UNSERIALIZABLE:" + value.getClass().getName() + "]";
        }
    }

    /**
     * 按配置的最大字符数截断大型日志内容。
     */
    public String truncate(String value) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "\n...[TRUNCATED]";
    }

    /**
     * 递归清洗 JSON 节点中的密码、Token、Secret 等敏感字段。
     *
     * @param node 待脱敏 JSON 节点
     */
    private void sanitize(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            List<String> fieldNames = new ArrayList<>();
            objectNode.fieldNames().forEachRemaining(fieldNames::add);
            for (String fieldName : fieldNames) {
                JsonNode child = objectNode.get(fieldName);
                if (isSensitive(fieldName)) {
                    objectNode.put(fieldName, "******");
                } else {
                    sanitize(child);
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            arrayNode.forEach(this::sanitize);
        }
    }

    /**
     * 判断字段名是否命中敏感字段规则。
     *
     * @param fieldName JSON 字段名称
     * @return 是否为敏感字段
     */
    private boolean isSensitive(String fieldName) {
        String normalized = fieldName == null ? "" : fieldName.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
        return SENSITIVE_EXACT.contains(normalized)
                || normalized.contains("password")
                || normalized.contains("token")
                || normalized.contains("secret")
                || normalized.contains("credential")
                || normalized.equals("authorization")
                || normalized.equals("cookie")
                || normalized.equals("setcookie")
                || normalized.equals("accesskey")
                || normalized.equals("apikey");
    }
}
