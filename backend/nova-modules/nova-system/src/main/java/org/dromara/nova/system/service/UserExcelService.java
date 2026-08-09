package org.dromara.nova.system.service;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.nova.system.dto.request.UserPageReqDto;
import org.dromara.nova.system.dto.response.ImportResultRespDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户 FastExcel 导入导出。
 */
public interface UserExcelService {
    /**
     * 输出用户导入 Excel 模板。
     */
    void template(HttpServletResponse response);

    /**
     * 按查询条件导出当前 Tenant 用户。
     */
    void export(UserPageReqDto query, HttpServletResponse response);

    /**
     * 读取 Excel 并批量导入当前 Tenant 用户。
     */
    ImportResultRespDto importUsers(MultipartFile file);
}
