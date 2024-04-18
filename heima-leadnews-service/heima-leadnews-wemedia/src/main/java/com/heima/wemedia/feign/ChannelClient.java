package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelClient implements IChannelClient {
    @Autowired
    private WmChannelService channelService;

    /**
     * 新增频道
     *
     * @param adChannel
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/save")
    public ResponseResult saveChannel(@RequestBody AdChannel adChannel) {
        return channelService.saveChannel(adChannel);
    }

    /**
     * 分页查询频道列表
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/list")
    public ResponseResult findListWithPage(@RequestBody ChannelDto dto) {
        return channelService.findListWithPage(dto);
    }

    /**
     * 更新频道信息
     *
     * @param channel
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/update")
    public ResponseResult updateChannel(@RequestBody AdChannel channel) {
        return channelService.updateChannel(channel);
    }
}
