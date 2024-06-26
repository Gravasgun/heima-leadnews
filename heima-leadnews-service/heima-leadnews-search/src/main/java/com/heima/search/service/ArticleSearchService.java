package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

public interface ArticleSearchService {
    /**
     * es文章分页检索
     * @return
     */
    ResponseResult search(UserSearchDto dto);
}
