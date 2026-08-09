package org.dromara.nova.file.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.file.dto.request.FilePageReqDto;
import org.dromara.nova.file.dto.response.FileRespDto;
import org.dromara.nova.file.model.FileDownload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件管理业务契约。
 */
public interface FileService {
    /**
     * 校验文件安全策略后上传并保存文件元数据。
     */
    FileRespDto upload(MultipartFile file);

    /**
     * 按查询条件分页返回数据。
     */
    PageResult<FileRespDto> page(FilePageReqDto request);

    /**
     * 按业务主键查询详情并执行权限与 Tenant 校验。
     */
    FileRespDto detail(Long fileId);

    /**
     * 校验访问权限后打开预览或下载流。
     */
    FileDownload open(Long fileId, boolean preview);

    /**
     * 删除或逻辑删除业务数据并执行关联校验。
     */
    void delete(Long fileId);

    /**
     * 批量删除文件并执行关联关系校验。
     */
    void batchDelete(List<Long> fileIds);

    /**
     * 绑定文件与业务数据关系。
     */
    void bind(Long fileId, String businessType, String businessId);

    /**
     * 解除文件与业务数据关系。
     */
    void unbind(Long fileId, String businessType, String businessId);

    /**
     * 校验文件集合在指定 Tenant 下是否可访问。
     */
    void assertAccessible(List<Long> fileIds, Long tenantId);
}
