package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.AdChannelDto;
import com.heima.model.admin.dtos.AdNewsAuthDto;
import com.heima.model.admin.dtos.AdSensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WemediaClient implements IWemediaClient {
    @Autowired
    private WmChannelService channelService;

    @Autowired
    private WmSensitiveService sensitiveService;

    @Autowired
    private WmNewsService newsService;

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
     * 查询所有频道
     *
     * @return
     */
    @Override
    @GetMapping("/api/v1/channel/findList")
    public ResponseResult findList() {
        return channelService.findAll();
    }

    /**
     * 分页查询频道列表
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/channel/list")
    public ResponseResult findListWithPage(@RequestBody AdChannelDto dto) {
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
    public ResponseResult deleteChannel(@PathVariable("id") Integer id) {
        return channelService.deleteChannel(id);
    }

    /**
     * 敏感词分页查询
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/list")
    public ResponseResult findSensitiveListPage(@RequestBody AdSensitiveDto dto) {
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

    /**
     * 修改敏感词
     *
     * @param sensitive
     * @return
     */
    @Override
    @PostMapping("/api/v1/sensitive/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitive sensitive) {
        return sensitiveService.updateSensitive(sensitive);
    }

    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("api/v1/sensitive/del/{id}")
    public ResponseResult deleteSensitive(@PathVariable("id") Integer id) {
        return sensitiveService.deleteSensitive(id);
    }

    /**
     * 管理端查询文章列表
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/news/list_vo")
    public ResponseResult listVo(@RequestBody AdNewsAuthDto dto) {
        return newsService.listVo(dto);
    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping("/api/v1/news/one_vo/{id}")
    public ResponseResult adminFindOneNews(@PathVariable Integer id) {
        return newsService.adminFindOneNews(id);
    }

    /**
     * 文章审核失败
     *
     * @param authDto
     * @return
     */
    @Override
    @PostMapping("/api/v1/news/auth_fail")
    public ResponseResult adminNewsAuthFail(@RequestBody AdNewsAuthDto authDto) {
        return newsService.adminNewsAuthFail(authDto);
    }

    /**
     * 文章审核成功
     *
     * @param authDto
     * @return
     */
    @Override
    @PostMapping("/api/v1/news/auth_pass")
    public ResponseResult adminNewsAuthPass(@RequestBody AdNewsAuthDto authDto) {
        return newsService.adminNewsAuthPass(authDto);
    }
}
