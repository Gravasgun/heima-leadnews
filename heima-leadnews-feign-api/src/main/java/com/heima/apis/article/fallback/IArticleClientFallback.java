package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleInfoDto;
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
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "保存article接口超时");
    }

    /**
     * 收藏文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectionArticle(CollectionBehaviorDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "收藏article接口超时");
    }

    /**
     * 加载文章行为-数据回显
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "文章数据回显接口超时");
    }

    /**
     * 热点文章定时计算
     */
    @Override
    public ResponseResult calculateHotArticle() {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "热点文章定时计算接口超时");
    }

    /**
     * 根据文章id查询文章
     *
     * @param id 文章id
     * @return
     */
    @Override
    public ResponseResult findArticleById(Long id) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "根据文章id查询文章接口超时");
    }

    /**
     * 根据文章id更新文章
     *
     * @param article
     * @return
     */
    @Override
    public ResponseResult updateArticle(ApArticle article) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "根据文章id更新文章接口超时");
    }
}
