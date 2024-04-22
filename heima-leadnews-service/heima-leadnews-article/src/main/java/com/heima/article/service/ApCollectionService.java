package com.heima.article.service;

import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollectionService {

    /**
     * 用户收藏文章功能
     * @param dto
     * @return
     */
     ResponseResult collection(CollectionBehaviorDto dto);
}
