package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 素材(图片)列表查询
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDto dto);

    /**
     * 根据id删除图片
     * @param id
     * @return
     */
    ResponseResult deleteMaterial(Long id);

    /**
     * 根据id收藏图片
     * @param id
     * @return
     */
    ResponseResult collectMaterial(Long id);

    /**
     * 取消收藏
     * @param id
     * @return
     */
    ResponseResult cancelCollectMaterial(Long id);
}