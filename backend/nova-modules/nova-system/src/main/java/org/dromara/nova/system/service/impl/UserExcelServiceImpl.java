package org.dromara.nova.system.service.impl;

import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.system.dto.request.DepartmentQueryReqDto;
import org.dromara.nova.system.dto.request.UserCreateReqDto;
import org.dromara.nova.system.dto.request.UserPageReqDto;
import org.dromara.nova.system.dto.response.ImportResultRespDto;
import org.dromara.nova.system.dto.response.RoleSimpleRespDto;
import org.dromara.nova.system.dto.response.UserRespDto;
import org.dromara.nova.system.excel.UserExcelRow;
import org.dromara.nova.system.service.DepartmentService;
import org.dromara.nova.system.service.RoleService;
import org.dromara.nova.system.service.UserExcelService;
import org.dromara.nova.system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * FastExcel 用户导入导出，不创建 ExcelUtils 包装层。
 */
@Service
@RequiredArgsConstructor
public class UserExcelServiceImpl implements UserExcelService {
    private static final int MAX_ROWS = 5000;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final RoleService roleService;

    /**
     * 输出用户导入 Excel 模板。
     *
     * @param response HTTP 响应对象，用于输出文件或导出数据
     */
    @Override
    public void template(HttpServletResponse response) {
        write(response, "用户导入模板.xlsx", List.of(new UserExcelRow()));
    }

    /**
     * 按查询条件和 DataScope 导出用户数据。
     *
     * @param query    MyBatis-Flex 查询条件
     * @param response HTTP 响应对象，用于输出文件或导出数据
     */
    @Override
    @OperationAudit(module = "USER", type = "EXPORT", description = "导出用户")
    public void export(UserPageReqDto query, HttpServletResponse response) {
        List<UserExcelRow> rows = new ArrayList<>();
        long page = 1;
        while (rows.size() < MAX_ROWS) {
            var result = userService.page(new UserPageReqDto(page, 200, query.username(), query.nickname(), query.phone(), query.email(), query.departmentId(), query.status()));
            for (var u : result.records()) rows.add(toRow(u));
            if (page * 200 >= result.total()) break;
            page++;
        }
        write(response, "用户数据.xlsx", rows);
    }

    /**
     * 解析 Excel 并批量导入当前 Tenant 用户。
     *
     * @param file 上传文件内容
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "USER", type = "IMPORT", description = "导入用户")
    public ImportResultRespDto importUsers(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "Excel 文件不能为空");
        List<UserExcelRow> rows = new ArrayList<>();
        try {
            FastExcel.read(file.getInputStream(), UserExcelRow.class, new AnalysisEventListener<UserExcelRow>() {
                @Override
                public void invoke(UserExcelRow row, AnalysisContext context) {
                    if (rows.size() >= MAX_ROWS)
                        throw new BusinessException(CommonResultCode.BAD_REQUEST, "导入最多 5000 行");
                    rows.add(row);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "Excel 解析失败");
        }
        int success = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            UserExcelRow row = rows.get(i);
            try {
                Long departmentId = findDepartment(row.getDepartmentCode());
                List<Long> roleIds = findRoles(row.getRoleCodes());
                userService.create(new UserCreateReqDto(row.getUsername(), row.getNickname(), row.getGender(), row.getPhone(), row.getEmail(), null, departmentId, row.getInitialPassword(), roleIds));
                success++;
            } catch (Exception e) {
                errors.add("第" + (i + 2) + "行：" + e.getMessage());
            }
        }
        return new ImportResultRespDto(rows.size(), success, rows.size() - success, errors);
    }

    /**
     * 将用户响应转换为 Excel 导出行。
     *
     * @param u 用户响应数据
     * @return 方法处理结果。
     */
    private UserExcelRow toRow(UserRespDto u) {
        UserExcelRow r = new UserExcelRow();
        r.setUsername(u.username());
        r.setNickname(u.nickname());
        r.setGender(u.gender());
        r.setPhone(u.phone());
        r.setEmail(u.email());
        r.setDepartmentCode(u.departmentCode());
        r.setRoleCodes(String.join(",", u.roles().stream().map(RoleSimpleRespDto::roleCode).toList()));
        r.setInitialPassword(null);
        return r;
    }

    /**
     * 根据部门编码解析当前 Tenant Department。
     *
     * @param code 编码
     * @return 业务计算结果。
     */
    private Long findDepartment(String code) {
        if (code == null || code.isBlank()) return null;
        for (var node : flatten(departmentService.tree(new DepartmentQueryReqDto(code, 1))))
            if (code.equals(node.getDepartmentCode())) return node.getId();
        throw new BusinessException(CommonResultCode.BAD_REQUEST, "Department 编码不存在: " + code);
    }

    /**
     * 将部门树展开为列表。
     *
     * @param roots部门根节点集合
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<org.dromara.nova.system.dto.response.DepartmentRespDto> flatten(List<org.dromara.nova.system.dto.response.DepartmentRespDto> roots) {
        List<org.dromara.nova.system.dto.response.DepartmentRespDto> out = new ArrayList<>();
        Deque<org.dromara.nova.system.dto.response.DepartmentRespDto> q = new ArrayDeque<>(roots);
        while (!q.isEmpty()) {
            var n = q.removeFirst();
            out.add(n);
            q.addAll(n.getChildren());
        }
        return out;
    }

    /**
     * 根据角色编码解析当前 Tenant 角色。
     *
     * @param codes 角色编码集合
     * @return 符合当前 Tenant、权限和 DataScope 约束的数据列表。
     */
    private List<Long> findRoles(String codes) {
        if (codes == null || codes.isBlank()) return List.of();
        List<Long> ids = new ArrayList<>();
        for (String code : codes.split(",")) {
            String c = code.trim();
            var role = roleService.options(c).stream().filter(r -> r.roleCode().equals(c)).findFirst().orElseThrow(() -> new BusinessException(CommonResultCode.BAD_REQUEST, "Role 编码不存在: " + c));
            ids.add(role.id());
        }
        return ids;
    }

    /**
     * 将 Excel 数据写入 HTTP 响应流。
     *
     * @param response HTTP 响应对象，用于输出文件或导出数据
     * @param filename 下载文件名
     * @param rows     Excel 数据行
     */
    private void write(HttpServletResponse response, String filename, List<UserExcelRow> rows) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            FastExcel.write(response.getOutputStream(), UserExcelRow.class).sheet("用户").doWrite(rows);
        } catch (IOException e) {
            throw new BusinessException(CommonResultCode.INTERNAL_ERROR, "Excel 导出失败");
        }
    }
}
