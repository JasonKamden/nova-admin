package org.dromara.nova.system.service;

import org.dromara.nova.common.core.model.PageResult;
import org.dromara.nova.system.dto.request.DictDataCreateReqDto;
import org.dromara.nova.system.dto.request.DictDataUpdateReqDto;
import org.dromara.nova.system.dto.request.DictTypeCreateReqDto;
import org.dromara.nova.system.dto.request.DictTypeUpdateReqDto;
import org.dromara.nova.system.dto.response.DictDataRespDto;
import org.dromara.nova.system.dto.response.DictTypeRespDto;

import java.util.List;

/**
 * 当前 Tenant 字典类型和字典数据维护业务契约。
 */
public interface DictionaryService {
    /**
     * 查询字典类型列表。
     */
    List<DictTypeRespDto> types(String keyword);

    /**
     * 新增字典类型。
     */
    DictTypeRespDto createType(DictTypeCreateReqDto request);

    /**
     * 修改字典类型。
     */
    DictTypeRespDto updateType(Long id, DictTypeUpdateReqDto request);

    /**
     * 删除字典类型，存在数据时拒绝删除。
     */
    void deleteType(Long id);

    /**
     * 分页查询指定字典类型下的数据。
     */
    PageResult<DictDataRespDto> data(Long typeId, long pageNum, long pageSize, String label, String value, Integer status);

    /**
     * 新增字典数据。
     */
    DictDataRespDto createData(Long typeId, DictDataCreateReqDto request);

    /**
     * 修改字典数据。
     */
    DictDataRespDto updateData(Long id, DictDataUpdateReqDto request);

    /**
     * 删除字典数据。
     */
    void deleteData(Long id);
}
