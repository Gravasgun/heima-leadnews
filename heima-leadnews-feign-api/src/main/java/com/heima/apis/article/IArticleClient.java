package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)
public interface IArticleClient {
    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto dto);

    /**
     * 收藏文章
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/collection_behavior")
    ResponseResult collectionArticle(@RequestBody CollectionBehaviorDto dto);

    /**
     * 加载文章行为-数据回显
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/article/load_article_behavior/")
    ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto dto);

    /**
     * 热点文章定时计算
     */
    @PostMapping("/api/v1/article/calculateHotArticle")
    ResponseResult calculateHotArticle();
}
