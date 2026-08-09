package org.dromara.nova.file.support;

import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.storage.config.StorageProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

/**
 * 文件上传安全策略：大小、文件名、危险扩展名与常见文件签名校验。
 */
@Component
@RequiredArgsConstructor
public class FileUploadPolicy {
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "exe", "dll", "bat", "cmd", "sh", "ps1", "msi", "jar", "class", "com", "scr"
    );
    private final StorageProperties storageProperties;

    /**
     * 校验上传文件大小、文件名、扩展名、MIME 与文件签名。
     *
     * @param file 上传文件内容
     * @return 方法处理结果。
     */
    public FileUploadMetadata validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException(CommonResultCode.FILE_ERROR, "文件不能为空");
        long max = storageProperties.maxFileSize() > 0 ? storageProperties.maxFileSize() : 50L * 1024 * 1024;
        if (file.getSize() > max) throw new BusinessException(CommonResultCode.FILE_ERROR, "文件大小超过系统限制");
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        if (original.indexOf('\0') >= 0) throw new BusinessException(CommonResultCode.FILE_ERROR, "文件名非法");
        String ext = extension(original);
        if (BLOCKED_EXTENSIONS.contains(ext))
            throw new BusinessException(CommonResultCode.FILE_ERROR, "禁止上传该文件类型");
        byte[] head = readHead(file);
        if (isExecutable(head)) throw new BusinessException(CommonResultCode.FILE_ERROR, "检测到危险可执行文件");
        validateKnownSignature(file.getContentType(), head);
        return new FileUploadMetadata(original, ext, file.getContentType());
    }

    /**
     * 读取上传文件头部字节用于文件签名识别。
     *
     * @param file 上传文件内容
     * @return 方法处理结果。
     */
    private byte[] readHead(MultipartFile file) {
        try (var input = file.getInputStream()) {
            return input.readNBytes(16);
        } catch (IOException e) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "文件读取失败");
        }
    }

    /**
     * 识别 PE、ELF 和脚本 Shebang 等危险可执行文件签名。
     *
     * @param b 文件头字节数组
     * @return 业务校验或处理结果。
     */
    private boolean isExecutable(byte[] b) {
        if (b.length >= 2 && b[0] == 'M' && b[1] == 'Z') return true;
        if (b.length >= 4 && (b[0] & 0xff) == 0x7f && b[1] == 'E' && b[2] == 'L' && b[3] == 'F') return true;
        return b.length >= 2 && b[0] == '#' && b[1] == '!';
    }

    /**
     * 校验常见 MIME 类型与文件头签名是否一致。
     *
     * @param contentType MIME 类型
     * @param b           文件头字节数组
     */
    private void validateKnownSignature(String contentType, byte[] b) {
        if (contentType == null) return;
        boolean known = switch (contentType.toLowerCase()) {
            case "image/jpeg" ->
                    b.length >= 3 && (b[0] & 0xff) == 0xff && (b[1] & 0xff) == 0xd8 && (b[2] & 0xff) == 0xff;
            case "image/png" -> b.length >= 8 && (b[0] & 0xff) == 0x89 && b[1] == 0x50 && b[2] == 0x4e && b[3] == 0x47;
            case "image/webp" -> b.length >= 12 && ascii(b, 0, 4).equals("RIFF") && ascii(b, 8, 4).equals("WEBP");
            case "application/pdf" -> b.length >= 5 && ascii(b, 0, 5).equals("%PDF-");
            default -> true;
        };
        if (!known) throw new BusinessException(CommonResultCode.FILE_ERROR, "文件内容与声明的 MIME 类型不一致");
    }

    /**
     * 将文件头指定区间按 US-ASCII 解码，用于魔数判断。
     *
     * @param b      文件头字节数组
     * @param offset 字节数组起始偏移
     * @param length 读取长度
     * @return 方法处理结果。
     */
    private String ascii(byte[] b, int offset, int length) {
        return new String(b, offset, length, StandardCharsets.US_ASCII);
    }

    /**
     * 从不可信原始文件名中提取并规范化扩展名。
     *
     * @param name 名称
     * @return 方法处理结果。
     */
    private String extension(String name) {
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        String safe = name.substring(slash + 1);
        int dot = safe.lastIndexOf('.');
        return dot < 0 ? "" : safe.substring(dot + 1).replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    /**
     * FileUploadMetadata 数据模型。
     *
     * @param originalName 上传时原始文件名
     * @param extension    文件扩展名
     * @param contentType  MIME 类型
     */
    public record FileUploadMetadata(String originalName, String extension, String contentType) {
    }
}
