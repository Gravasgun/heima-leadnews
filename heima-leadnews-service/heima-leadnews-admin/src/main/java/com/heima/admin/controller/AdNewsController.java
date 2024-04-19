package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class AdNewsController {
    @Autowired
    private IWemediaClient wemediaClient;

    @PostMapping("/api/v1/news/list_vo")
    public ResponseResult listVo(@RequestBody NewsAuthDto dto) {
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
}
