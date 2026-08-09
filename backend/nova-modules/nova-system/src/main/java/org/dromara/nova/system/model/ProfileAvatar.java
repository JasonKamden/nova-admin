package org.dromara.nova.system.model;

import java.io.InputStream;

/**
 * 当前用户头像流。
 *
 * @param contentType MIME 类型
 * @param size        文件大小，单位字节
 * @param input       文件输入流
 */
public record ProfileAvatar(String contentType, long size, InputStream input) {
}
