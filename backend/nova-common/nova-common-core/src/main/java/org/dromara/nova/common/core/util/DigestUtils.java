package org.dromara.nova.common.core.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * SHA-256 摘要工具。
 */
public final class DigestUtils {
    private DigestUtils() {
    }

    /**
     * 计算输入内容的 SHA-256 十六进制摘要。
     *
     * @param value 待存储或处理的值
     * @return SHA-256 十六进制摘要
     */
    public static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("计算 SHA-256 失败", e);
        }
    }

    /**
     * 计算输入内容的 SHA-256 十六进制摘要。
     *
     * @param input 对象输入流
     * @return SHA-256 十六进制摘要
     */
    public static String sha256(InputStream input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int len;
            while ((len = input.read(buffer)) > 0) digest.update(buffer, 0, len);
            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new IllegalStateException("计算 SHA-256 失败", e);
        }
    }
}
