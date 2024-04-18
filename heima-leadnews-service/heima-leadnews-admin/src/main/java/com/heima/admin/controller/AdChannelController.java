package com.heima.admin.controller;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController {
    @Autowired
    private IChannelClient channelClient;

    @PostMapping("/save")
    public ResponseResult saveChannel(@RequestBody AdChannel adChannel) {
        return channelClient.saveChannel(adChannel);
    }

    @PostMapping("/list")
    public ResponseResult findListWithPage(@RequestBody ChannelDto dto) {
        return channelClient.findListWithPage(dto);
    }

    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody AdChannel channel) {
        return channelClient.updateChannel(channel);
    }

    @GetMapping("/del/{id}")
    public ResponseResult deleteChannel(@PathVariable("id") Integer id) {
        return channelClient.del(id);
    }
}

