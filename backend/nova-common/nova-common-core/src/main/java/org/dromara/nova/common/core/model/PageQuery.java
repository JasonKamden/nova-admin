package org.dromara.nova.common.core.model;

/**
 * 通用分页查询参数。
 *
 * @param pageNum  页码，从 1 开始
 * @param pageSize 每页条数
 */
public record PageQuery(long pageNum, long pageSize) {
    public PageQuery {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 200) pageSize = 200;
    }
}
