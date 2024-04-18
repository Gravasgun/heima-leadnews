package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     *
     * @return
     */
    ResponseResult findAll();


    /**
     * 新增频道
     *
     * @return
     */
    ResponseResult saveChannel(AdChannel adChannel);

    /**
     * 分页查询频道列表
     *
     * @param dto
     * @return
     */
    ResponseResult findListWithPage(ChannelDto dto);

    /**
     * 更新频道信息
     *
     * @param channel
     * @return
     */
    ResponseResult updateChannel(AdChannel channel);

    /**
     * 删除频道
     *
     * @param id
     * @return
     */
    ResponseResult del(Integer id);
}