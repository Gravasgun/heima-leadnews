package com.heima.apis.wemedia;

import com.heima.model.admin.beans.AdChannel;
import com.heima.model.admin.dtos.AdChannelDto;
import com.heima.model.admin.dtos.AdNewsAuthDto;
import com.heima.model.admin.dtos.AdSensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "leadnews-wemedia")
public interface IWemediaClient {
    /**
     * 新增频道
     *
     * @return
     */
    @PostMapping("/api/v1/channel/save")
    ResponseResult saveChannel(@RequestBody AdChannel adChannel);

    /**
     * 查询所有频道
     *
     * @return
     */
    @GetMapping("/api/v1/channel/findList")
    ResponseResult findList();

    /**
     * 分页查询频道列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/channel/list")
    ResponseResult findListWithPage(@RequestBody AdChannelDto dto);

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
    ResponseResult deleteChannel(@PathVariable("id") Integer id);

    /**
     * 敏感词分页查询
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/sensitive/list")
    ResponseResult findSensitiveListPage(@RequestBody AdSensitiveDto dto);

    /**
     * 新增敏感词
     *
     * @param sensitive
     * @return
     */
    @PostMapping("/api/v1/sensitive/save")
    ResponseResult saveSensitive(@RequestBody WmSensitive sensitive);

    /**
     * 修改敏感词
     *
     * @param sensitive
     * @return
     */
    @PostMapping("/api/v1/sensitive/update")
    ResponseResult updateSensitive(@RequestBody WmSensitive sensitive);

    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    ResponseResult deleteSensitive(@PathVariable("id") Integer id);

    /**
     * 管理端查询文章列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/news/list_vo")
    ResponseResult listVo(@RequestBody AdNewsAuthDto dto);

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/one_vo/{id}")
    ResponseResult adminFindOneNews(@PathVariable Integer id);

    /**
     * 文章审核失败
     *
     * @param authDto
     * @return
     */
    @PostMapping("/api/v1/news/auth_fail")
    ResponseResult adminNewsAuthFail(@RequestBody AdNewsAuthDto authDto);

    /**
     * 文章审核成功
     *
     * @param authDto
     * @return
     */
    @PostMapping("/api/v1/news/auth_pass")
    ResponseResult adminNewsAuthPass(@RequestBody AdNewsAuthDto authDto);
}
