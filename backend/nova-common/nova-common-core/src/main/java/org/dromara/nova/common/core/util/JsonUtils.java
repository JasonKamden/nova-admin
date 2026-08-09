package org.dromara.nova.common.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;

/**
 * JSON 序列化工具，仅封装项目统一 ObjectMapper。
 */
public final class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private JsonUtils() {
    }

    /**
     * 注入 Spring 管理的 ObjectMapper 作为项目统一 JSON 序列化器。
     *
     * @param mapper ObjectMapper
     */
    public static void configure(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 待存储或处理的值
     * @return JSON 字符串
     */
    public static String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(CommonResultCode.INTERNAL_ERROR, "JSON 序列化失败");
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定 Java 类型。
     *
     * @param value 待存储或处理的值
     * @param type  目标 Java 类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String value, Class<T> type) {
        if (value == null || value.isBlank()) return null;
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "JSON 格式错误");
        }
    }
}
