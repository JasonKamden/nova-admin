package org.dromara.nova.file.model;

import java.io.InputStream;

/**
 * 文件读取结果。
 *
 * @param filename    下载文件名
 * @param contentType MIME 类型
 * @param size        文件大小，单位字节
 * @param input       文件输入流
 */
public record FileDownload(String filename, String contentType, long size, InputStream input) {
}
