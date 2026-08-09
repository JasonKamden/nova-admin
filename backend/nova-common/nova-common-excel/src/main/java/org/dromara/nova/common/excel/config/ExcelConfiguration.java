package org.dromara.nova.common.excel.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FastExcel 配置。
 */
@Configuration
@EnableConfigurationProperties(ExcelProperties.class)
public class ExcelConfiguration {
}
