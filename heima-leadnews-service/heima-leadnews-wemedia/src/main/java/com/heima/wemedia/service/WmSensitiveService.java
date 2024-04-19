package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdSensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;

import java.util.List;

public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * 查询所有敏感词
     * @return
     */
    List<WmSensitive> findAllSensitives();
    /**
     * 敏感词分页查询
     *
     * @param dto
     * @return
     */
    ResponseResult list(AdSensitiveDto dto);

    /**
     * 新增敏感词
     *
     * @param sensitive
     * @return
     */
    ResponseResult saveSensitive(WmSensitive sensitive);

    /**
     * 修改敏感词
     *
     * @param sensitive
     * @return
     */
    ResponseResult updateSensitive(WmSensitive sensitive);

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    ResponseResult deleteSensitive(Integer id);
}
