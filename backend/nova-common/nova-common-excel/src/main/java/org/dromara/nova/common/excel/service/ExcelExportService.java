package org.dromara.nova.common.excel.service;

import cn.idev.excel.FastExcel;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

/**
 * 基于 FastExcel 的通用流式导出。
 */
@Service
public class ExcelExportService {
    /**
     * 使用 FastExcel 将业务数据写入输出流。
     *
     * @param output    Excel 输出流
     * @param headType  Excel 行模型类型
     * @param sheetName 工作表名称
     * @param rows      待写入数据行
     */
    public <T> void write(OutputStream output, Class<T> headType, String sheetName, List<T> rows) {
        FastExcel.write(output, headType).sheet(sheetName).doWrite(rows);
    }
}
