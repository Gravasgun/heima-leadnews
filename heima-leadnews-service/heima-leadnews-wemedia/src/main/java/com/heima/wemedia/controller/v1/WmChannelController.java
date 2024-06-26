package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {
    @Autowired
    private WmChannelService channelService;

    /**
     * 查询所有频道
     *
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult findAll() {
        return channelService.findAll();
    }
}
