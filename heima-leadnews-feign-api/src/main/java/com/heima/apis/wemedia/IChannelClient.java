package com.heima.apis.wemedia;

import com.heima.model.admin.beans.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
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
}
