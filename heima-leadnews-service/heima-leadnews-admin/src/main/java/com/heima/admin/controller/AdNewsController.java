package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.dtos.AdNewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class AdNewsController {
    @Autowired
    private IWemediaClient wemediaClient;

    @PostMapping("/api/v1/news/list_vo")
    public ResponseResult listVo(@RequestBody AdNewsAuthDto dto) {
        return wemediaClient.listVo(dto);
    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/one_vo/{id}")
    public ResponseResult adminFindOneNews(@PathVariable Integer id) {
        return wemediaClient.adminFindOneNews(id);
    }

    /**
     * 文章审核失败
     *
     * @param authDto
     * @return
     */

    @PostMapping("/api/v1/news/auth_fail")
    public ResponseResult adminNewsAuthFail(@RequestBody AdNewsAuthDto authDto) {
        return wemediaClient.adminNewsAuthFail(authDto);
    }
}
