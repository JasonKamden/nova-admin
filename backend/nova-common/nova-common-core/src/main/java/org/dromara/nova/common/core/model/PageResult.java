package org.dromara.nova.common.core.model;

import java.util.List;

/**
 * 统一分页结果。
 *
 * @param records  当前页数据
 * @param total    总记录数
 * @param pageNum  当前页码，从 1 开始
 * @param pageSize 每页条数
 * @param <T>      业务响应类型
 */
public record PageResult<T>(List<T> records, long total, long pageNum, long pageSize) {
    /**
     * 创建统一分页结果。
     */
    public static <T> PageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }
}
