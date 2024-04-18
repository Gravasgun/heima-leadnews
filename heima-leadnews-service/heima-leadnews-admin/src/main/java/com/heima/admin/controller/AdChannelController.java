package com.heima.admin.controller;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController {
    @Autowired
    private IChannelClient channelClient;

    @PostMapping("/save")
    public ResponseResult saveChannel(AdChannel adChannel) {
        return channelClient.saveChannel(adChannel);
    }

    @PostMapping("/list")
    public ResponseResult findListWithPage(@RequestBody ChannelDto dto) {
        return channelClient.findListWithPage(dto);
    }
}

