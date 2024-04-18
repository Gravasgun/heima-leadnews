package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController {
    @Autowired
    private IWemediaClient wemediaClient;

    @PostMapping("/save")
    public ResponseResult saveChannel(@RequestBody AdChannel adChannel) {
        return wemediaClient.saveChannel(adChannel);
    }

    @PostMapping("/list")
    public ResponseResult findListWithPage(@RequestBody ChannelDto dto) {
        return wemediaClient.findListWithPage(dto);
    }

    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody AdChannel channel) {
        return wemediaClient.updateChannel(channel);
    }

    @GetMapping("/del/{id}")
    public ResponseResult deleteChannel(@PathVariable("id") Integer id) {
        return wemediaClient.del(id);
    }
}

