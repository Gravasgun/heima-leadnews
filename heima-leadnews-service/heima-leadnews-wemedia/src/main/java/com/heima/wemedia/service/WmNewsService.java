package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdNewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 发布文章
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(WmNewsDto dto);

    /**
     * 文章上下架
     *
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 管理端查询文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult listVo(AdNewsAuthDto dto);

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    ResponseResult adminFindOneNews(Integer id);

    /**
     * 文章审核失败
     * @param authDto
     * @return
     */
    ResponseResult adminNewsAuthFail(AdNewsAuthDto authDto);

    /**
     * 文章审核通过
     * @param authDto
     * @return
     */
    ResponseResult adminNewsAuthPass(AdNewsAuthDto authDto);
}