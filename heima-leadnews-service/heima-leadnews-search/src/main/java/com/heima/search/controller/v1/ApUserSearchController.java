package com.heima.search.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.beans.HistorySearchDto;
import com.heima.search.service.ApUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * APP用户搜索信息表
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController {
    @Autowired
    private ApUserSearchService apUserSearchService;

    @PostMapping("/load")
    public ResponseResult findUserSearchHistory() {
        return apUserSearchService.findUserSearchHistory();
    }

    @PostMapping("/del")
    public ResponseResult delUserSearch(@RequestBody String dto) {
        Map map = JSONObject.parseObject(dto, Map.class);
        String id = (String) map.get("id");
        return apUserSearchService.delUserSearch(id);
    }
}