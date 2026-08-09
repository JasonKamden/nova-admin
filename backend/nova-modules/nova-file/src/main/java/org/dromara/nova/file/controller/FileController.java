package org.dromara.nova.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.core.model.R;
import org.dromara.nova.file.dto.request.FileBatchDeleteReqDto;
import org.dromara.nova.file.dto.request.FilePageReqDto;
import org.dromara.nova.file.dto.response.FileRespDto;
import org.dromara.nova.file.model.FileDownload;
import org.dromara.nova.file.service.FileService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理、上传、预览、下载。
 */
@Tag(name = "文件与附件", description = "提供当前 Context 下的文件上传、分页、详情、预览、下载和删除能力。")
@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * 上传文件。
     *
     * @param file 上传文件
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @PostMapping("/api/files/upload")
    @SaCheckPermission("file:upload")
    @Operation(summary = "上传文件", description = "上传文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<FileRespDto> upload(@Parameter(description = "上传文件", required = true) @RequestPart("file") MultipartFile file) {
        return R.ok(fileService.upload(file));
    }

    /**
     * 分页查询文件。
     *
     * @param req FilePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/api/system/files")
    @SaCheckPermission("file:list")
    @Operation(summary = "分页查询文件", description = "分页查询文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<PageResult<FileRespDto>> page(@ParameterObject @Valid FilePageReqDto req) {
        return R.ok(fileService.page(req));
    }

    /**
     * 查询文件详情。
     *
     * @param id 文件 ID
     * @return 统一响应对象；data 字段结构及字段含义由对应响应 DTO 的 @Schema / JavaDoc 定义。
     */
    @GetMapping("/api/files/{id}")
    @SaCheckPermission("file:list")
    @Operation(summary = "查询文件详情", description = "查询文件详情。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<FileRespDto> detail(@Parameter(description = "文件 ID", required = true) @PathVariable Long id) {
        return R.ok(fileService.detail(id));
    }

    /**
     * 预览文件。
     *
     * @param id 文件 ID
     * @return 文件流 HTTP 响应，包含 Content-Type、Content-Length 和 Content-Disposition。
     */
    @GetMapping("/api/files/{id}/preview")
    @SaCheckPermission("file:preview")
    @Operation(summary = "预览文件", description = "预览文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public ResponseEntity<InputStreamResource> preview(@Parameter(description = "文件 ID", required = true) @PathVariable Long id) {
        return response(fileService.open(id, true), true);
    }

    /**
     * 下载文件。
     *
     * @param id 文件 ID
     * @return 文件流 HTTP 响应，包含 Content-Type、Content-Length 和 Content-Disposition。
     */
    @GetMapping("/api/files/{id}/download")
    @SaCheckPermission("file:download")
    @Operation(summary = "下载文件", description = "下载文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public ResponseEntity<InputStreamResource> download(@Parameter(description = "文件 ID", required = true) @PathVariable Long id) {
        return response(fileService.open(id, false), false);
    }

    /**
     * 删除文件。
     *
     * @param id 文件 ID
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/api/system/files/{id}")
    @SaCheckPermission("file:delete")
    @Operation(summary = "删除文件", description = "删除文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> delete(@Parameter(description = "文件 ID", required = true) @PathVariable Long id) {
        fileService.delete(id);
        return R.ok();
    }

    /**
     * 批量删除文件。
     *
     * @param req FileBatchDeleteReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 统一成功响应；无业务响应体。
     */
    @DeleteMapping("/api/system/files/batch")
    @SaCheckPermission("file:delete")
    @Operation(summary = "批量删除文件", description = "批量删除文件。接口按当前登录用户、PLATFORM/TENANT Context、权限和 DataScope 执行服务端校验。")
    public R<Void> batch(@Valid @RequestBody FileBatchDeleteReqDto req) {
        fileService.batchDelete(req.fileIds());
        return R.ok();
    }

    /**
     * 构造文件预览或下载 HTTP 响应，并安全编码下载文件名。
     *
     * @param f      已通过权限校验的文件下载模型
     * @param inline true 表示浏览器内联预览，false 表示附件下载
     * @return 包含内容类型、长度、Content-Disposition 与文件流的 HTTP 响应
     */
    private ResponseEntity<InputStreamResource> response(FileDownload f, boolean inline) {
        String filename = URLEncoder.encode(f.filename(), StandardCharsets.UTF_8).replace("+", "%20");
        MediaType type;
        try {
            type = MediaType.parseMediaType(f.contentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : f.contentType());
        } catch (Exception e) {
            type = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok().contentType(type).contentLength(f.size()).header(HttpHeaders.CONTENT_DISPOSITION, (inline ? "inline" : "attachment") + "; filename*=UTF-8''" + filename).body(new InputStreamResource(f.input()));
    }
}
