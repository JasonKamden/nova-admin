package org.dromara.nova.common.excel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FastExcel 批量处理配置。
 *
 * @param maxRows Excel 最大允许处理行数
 */
@ConfigurationProperties(prefix = "excel")
public record ExcelProperties(
        /** 单次导入/导出允许处理的最大数据行数，用于防止无边界内存和响应压力。 */
        int maxRows
) {
}
