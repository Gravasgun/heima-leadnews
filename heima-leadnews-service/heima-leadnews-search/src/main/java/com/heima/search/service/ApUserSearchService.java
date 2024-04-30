package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.beans.HistorySearchDto;

public interface ApUserSearchService {
    /**
     * 查询用户搜索历史记录
     *
     * @return
     */
    ResponseResult findUserSearchHistory();

    /**
     * 保存用户搜索历史记录
     *
     * @param keyword
     * @param userId
     */
    void insert(String keyword, Integer userId);

    /**
     删除搜索历史
     @param id
     @return
     */
    ResponseResult delUserSearch(String id);
}