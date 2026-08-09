package org.dromara.nova.file.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import org.dromara.nova.common.core.enums.CommonResultCode;
import org.dromara.nova.common.core.exception.BusinessException;
import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.common.log.annotation.OperationAudit;
import org.dromara.nova.common.mybatis.support.AuditEntitySupport;
import org.dromara.nova.common.security.util.LoginUserUtils;
import org.dromara.nova.common.storage.service.StorageService;
import org.dromara.nova.common.tenant.context.TenantContextSupport;
import org.dromara.nova.file.dto.request.FilePageReqDto;
import org.dromara.nova.file.dto.response.FileRespDto;
import org.dromara.nova.file.entity.FileEntity;
import org.dromara.nova.file.entity.FileRelationEntity;
import org.dromara.nova.file.mapper.FileMapper;
import org.dromara.nova.file.mapper.FileRelationMapper;
import org.dromara.nova.file.model.FileDownload;
import org.dromara.nova.file.service.FileService;
import org.dromara.nova.file.support.FileUploadPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.dromara.nova.file.entity.table.FileEntityTableDef.FILE_ENTITY;
import static org.dromara.nova.file.entity.table.FileRelationEntityTableDef.FILE_RELATION_ENTITY;

/**
 * 文件元数据 + 对象存储完整实现。
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;
    private final Converter converter;
    private final FileRelationMapper fileRelationMapper;
    private final StorageService storageService;
    private final FileUploadPolicy fileUploadPolicy;

    /**
     * 校验文件安全策略后上传文件并保存文件元数据。
     *
     * @param file 上传文件内容
     * @return 业务响应 DTO。
     */
    @Override
    @OperationAudit(module = "FILE", type = "UPLOAD", description = "上传文件")
    public FileRespDto upload(MultipartFile file) {
        var meta = fileUploadPolicy.validate(file);
        String original = meta.originalName();
        String ext = meta.extension();
        String objectPath = objectPath(ext);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = new DigestInputStream(file.getInputStream(), digest)) {
                var stored = storageService.store(objectPath, original, file.getContentType(), file.getSize(), input);
                FileEntity e = new FileEntity();
                var context = TenantContextSupport.current();
                e.setContextType(context.contextType().name());
                e.setTenantId(context.tenantId());
                e.setOwnerUserId(LoginUserUtils.getUserId());
                e.setOriginalName(original);
                e.setExtension(ext);
                e.setContentType(file.getContentType());
                e.setFileSize(file.getSize());
                e.setSha256(HexFormat.of().formatHex(digest.digest()));
                e.setStorageType(stored.storageType());
                e.setStoragePath(stored.storagePath());
                e.setStatus(1);
                AuditEntitySupport.created(e, LoginUserUtils.getUserId());
                fileMapper.insert(e);
                return resp(e);
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "文件上传失败");
        }
    }

    /**
     * 按查询条件分页查询业务数据，并执行 Tenant、Permission 和 DataScope 约束。
     *
     * @param req FilePageReqDto 请求参数；各字段含义见 DTO @Schema 与 record @param 说明
     * @return 分页业务数据。
     */
    @Override
    public PageResult<FileRespDto> page(FilePageReqDto req) {
        QueryWrapper q = contextQuery();
        if (req.fileName() != null && !req.fileName().isBlank()) q.and(FILE_ENTITY.ORIGINAL_NAME.like(req.fileName()));
        if (req.contentType() != null && !req.contentType().isBlank())
            q.and(FILE_ENTITY.CONTENT_TYPE.like(req.contentType()));
        if (req.storageType() != null && !req.storageType().isBlank())
            q.and(FILE_ENTITY.STORAGE_TYPE.eq(req.storageType()));
        if (req.status() != null) q.and(FILE_ENTITY.STATUS.eq(req.status()));
        Page<FileEntity> p = fileMapper.paginate(req.pageNum(), req.pageSize(), q.orderBy(FILE_ENTITY.ID.desc()));
        return PageResult.of(converter.convert(p.getRecords(), FileRespDto.class), p.getTotalRow(), p.getPageNumber(), p.getPageSize());
    }

    /**
     * 按业务主键查询详情并执行 Tenant 与权限校验。
     *
     * @param id 主键 ID
     * @return 业务响应 DTO。
     */
    @Override
    public FileRespDto detail(Long id) {
        return resp(require(id));
    }

    /**
     * 校验文件访问权限后打开预览或下载流。
     *
     * @param id      主键 ID
     * @param preview 是否按在线预览方式打开
     * @return 方法处理结果。
     */
    @Override
    public FileDownload open(Long id, boolean preview) {
        FileEntity e = require(id);
        if (preview && !previewable(e.getContentType()))
            throw new BusinessException(CommonResultCode.FILE_ERROR, "该类型不支持在线预览");
        try {
            return new FileDownload(e.getOriginalName(), e.getContentType(), e.getFileSize(), storageService.open(e.getStoragePath()));
        } catch (IOException ex) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "文件读取失败");
        }
    }

    /**
     * 删除或逻辑删除业务数据，并执行引用关系和权限校验。
     *
     * @param id 主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "FILE", type = "DELETE", description = "删除文件")
    public void delete(Long id) {
        FileEntity e = require(id);
        if (fileRelationMapper.selectCountByQuery(QueryWrapper.create().where(FILE_RELATION_ENTITY.FILE_ID.eq(id))) > 0)
            throw new BusinessException(CommonResultCode.CONFLICT, "文件已被业务引用，不能删除");
        try {
            storageService.delete(e.getStoragePath());
        } catch (IOException ex) {
            throw new BusinessException(CommonResultCode.FILE_ERROR, "对象存储删除失败");
        }
        e.setDeleted(true);
        e.setStatus(0);
        AuditEntitySupport.updated(e, LoginUserUtils.getUserId());
        fileMapper.update(e);
        for (FileRelationEntity r : fileRelationMapper.selectListByQuery(QueryWrapper.create().where(FILE_RELATION_ENTITY.FILE_ID.eq(id))))
            fileRelationMapper.deleteById(r.getId());
    }

    /**
     * 批量删除业务数据并逐项执行权限和关联校验。
     *
     * @param ids 业务主键 ID 集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationAudit(module = "FILE", type = "BATCH_DELETE", description = "批量删除文件")
    public void batchDelete(List<Long> ids) {
        ids.stream().distinct().forEach(this::delete);
    }

    /**
     * 绑定文件与业务数据的关联关系。
     *
     * @param fileId       文件 ID
     * @param businessType 业务类型
     * @param businessId   业务数据主键
     */
    @Override
    public void bind(Long fileId, String businessType, String businessId) {
        FileEntity e = require(fileId);
        if (businessType == null || businessType.isBlank() || businessId == null || businessId.isBlank())
            throw new BusinessException(CommonResultCode.BAD_REQUEST, "业务关联不能为空");
        if (fileRelationMapper.selectCountByQuery(QueryWrapper.create().where(FILE_RELATION_ENTITY.FILE_ID.eq(fileId)).and(FILE_RELATION_ENTITY.BUSINESS_TYPE.eq(businessType)).and(FILE_RELATION_ENTITY.BUSINESS_ID.eq(businessId))) > 0)
            return;
        FileRelationEntity r = new FileRelationEntity();
        r.setTenantId(e.getTenantId());
        r.setFileId(fileId);
        r.setBusinessType(businessType);
        r.setBusinessId(businessId);
        r.setCreateBy(LoginUserUtils.getUserId());
        r.setCreateTime(LocalDateTime.now());
        fileRelationMapper.insert(r);
    }

    /**
     * 解除文件与业务数据的关联关系。
     *
     * @param fileId       文件 ID
     * @param businessType 业务类型
     * @param businessId   业务数据主键
     */
    @Override
    public void unbind(Long fileId, String businessType, String businessId) {
        for (FileRelationEntity relation : fileRelationMapper.selectListByQuery(QueryWrapper.create().where(FILE_RELATION_ENTITY.FILE_ID.eq(fileId)).and(FILE_RELATION_ENTITY.BUSINESS_TYPE.eq(businessType)).and(FILE_RELATION_ENTITY.BUSINESS_ID.eq(businessId))))
            fileRelationMapper.deleteById(relation.getId());
    }

    /**
     * 校验文件集合在指定 Tenant 下是否允许当前业务使用。
     *
     * @param ids      业务主键 ID 集合
     * @param tenantId Tenant ID；Tenant 业务写入以服务端可信 Context 为准
     */
    @Override
    public void assertAccessible(List<Long> ids, Long tenantId) {
        if (ids == null || ids.isEmpty()) return;
        for (Long id : new LinkedHashSet<>(ids)) {
            FileEntity e = fileMapper.selectOneByQuery(QueryWrapper.create().where(FILE_ENTITY.ID.eq(id)).and(FILE_ENTITY.DELETED.eq(false)));
            if (e == null || !Objects.equals(e.getTenantId(), tenantId))
                throw new BusinessException(CommonResultCode.FORBIDDEN, "包含不可访问文件");
        }
    }

    /**
     * 构造按当前 PLATFORM/TENANT Context 隔离的文件基础查询条件。
     *
     * @return 方法处理结果。
     */
    private QueryWrapper contextQuery() {
        var c = TenantContextSupport.current();
        QueryWrapper q = QueryWrapper.create().where(FILE_ENTITY.DELETED.eq(false));
        if (c.isTenant()) q.and(FILE_ENTITY.TENANT_ID.eq(c.tenantId()));
        else q.and(FILE_ENTITY.CONTEXT_TYPE.eq("PLATFORM"));
        return q;
    }

    /**
     * 加载业务对象，不存在或越权时抛出业务异常。
     *
     * @param id 主键 ID
     * @return 已校验的内部业务实体，仅供服务内部使用。
     */
    private FileEntity require(Long id) {
        FileEntity e = fileMapper.selectOneByQuery(contextQuery().and(FILE_ENTITY.ID.eq(id)));
        if (e == null) throw new BusinessException(CommonResultCode.NOT_FOUND, "文件不存在或无权访问");
        return e;
    }

    /**
     * 将实体转换为对外响应 DTO。
     *
     * @param e 文件实体
     * @return 业务响应 DTO。
     */
    private FileRespDto resp(FileEntity e) {
        return converter.convert(e, FileRespDto.class);
    }

    /**
     * 生成不信任原始文件名的安全对象存储路径。
     *
     * @param ext 规范化文件扩展名
     * @return 方法处理结果。
     */
    private String objectPath(String ext) {
        var c = TenantContextSupport.current();
        String scope = c.isTenant() ? "tenant/" + c.tenantId() : "platform";
        return scope + "/" + LocalDate.now() + "/" + UUID.randomUUID() + (ext.isBlank() ? "" : "." + ext);
    }

    /**
     * 判断 MIME 类型是否允许浏览器在线预览。
     *
     * @param type 类型
     * @return 业务校验或处理结果。
     */
    private boolean previewable(String type) {
        return type != null && (type.startsWith("image/") || type.equals("application/pdf") || type.startsWith("text/"));
    }
}
