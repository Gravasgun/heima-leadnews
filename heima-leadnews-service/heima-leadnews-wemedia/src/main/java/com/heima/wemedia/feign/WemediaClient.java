package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WemediaClient implements IWemediaClient {
    @Autowired
    private WmChannelService channelService;

    @Autowired
    private WmSensitiveService sensitiveService;

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

    /**
     * 删除频道
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping("/api/v1/channel/del/{id}")
    public ResponseResult del(@PathVariable("id") Integer id) {
        return channelService.del(id);
    }

    /**
     * 敏感词分页查询
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/list")
    public ResponseResult findSensitiveListPage(@RequestBody SensitiveDto dto) {
        return sensitiveService.list(dto);
    }

    /**
     * 新增敏感词
     *
     * @param sensitive
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitive sensitive) {
        return sensitiveService.saveSensitive(sensitive);
    }
}