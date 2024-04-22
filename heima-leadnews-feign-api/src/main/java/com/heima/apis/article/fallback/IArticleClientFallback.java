package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class IArticleClientFallback implements IArticleClient {
    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"保存article接口超时");
    }

    /**
     * 收藏文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectionArticle(CollectionBehaviorDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"保存article接口超时");
    }
}
