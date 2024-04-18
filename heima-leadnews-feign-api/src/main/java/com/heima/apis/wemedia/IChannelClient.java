package com.heima.apis.wemedia;

import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-wemedia")
public interface IChannelClient {
    /**
     * 新增频道
     *
     * @return
     */
    @PostMapping("/api/v1/channel/save")
    ResponseResult saveChannel(@RequestBody AdChannel adChannel);

    /**
     * 分页查询频道列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/channel/list")
    ResponseResult findListWithPage(@RequestBody ChannelDto dto);

    /**
     * 更新频道信息
     *
     * @param channel
     * @return
     */
    @PostMapping("/api/v1/channel/update")
    ResponseResult updateChannel(@RequestBody AdChannel channel);

    /**
     * 删除频道
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/channel/del/{id}")
    ResponseResult del(@PathVariable("id") Integer id);

}