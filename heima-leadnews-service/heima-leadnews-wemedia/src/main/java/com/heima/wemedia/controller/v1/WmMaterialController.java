package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {
    @Autowired
    private WmMaterialService materialService;

    /**
     * 图片上传功能
     *
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return materialService.uploadPicture(multipartFile);
    }

    /**
     * 图片列表查询功能
     *
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto dto) {
        return materialService.findList(dto);
    }

    /**
     * 根据id删除图片
     *
     * @param id
     * @return
     */
    @GetMapping("del_picture/{id}")
    public ResponseResult deleteMaterial(@PathVariable("id") Long id) {
        return materialService.deleteMaterial(id);
    }

    /**
     * 根据id收藏图片
     *
     * @param id
     * @return
     */
    @GetMapping("collect/{id}")
    public ResponseResult collectMaterial(@PathVariable("id") Long id) {
        return materialService.collectMaterial(id);
    }

    /**
     * 取消收藏图片
     *
     * @param id
     * @return
     */
    @GetMapping("cancel_collect/{id}")
    public ResponseResult cancelCollectMaterial(@PathVariable("id") Long id) {
        return materialService.cancelCollectMaterial(id);
    }
}
