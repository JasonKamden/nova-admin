package org.dromara.nova.system.excel;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 用户导入导出 Excel 行。
 */
@Data
public class UserExcelRow {
    @ExcelProperty("账号")
    private String username;
    @ExcelProperty("姓名/昵称")
    private String nickname;
    @ExcelProperty("性别")
    private String gender;
    @ExcelProperty("手机号")
    private String phone;
    @ExcelProperty("邮箱")
    private String email;
    @ExcelProperty("Department编码")
    private String departmentCode;
    @ExcelProperty("角色编码(逗号分隔)")
    private String roleCodes;
    @ExcelProperty("初始密码(仅导入)")
    private String initialPassword;
}
