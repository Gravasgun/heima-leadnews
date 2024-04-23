package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     *
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

    /**
     * 根据文章id查询文章
     *
     * @param id 文章id
     * @return
     */
    @GetMapping("/api/v1/article/{id}")
    ResponseResult findArticleById(@PathVariable("id") Long id);

    /**
     * 根据文章id更新文章
     *
     * @param article
     * @return
     */
    @PostMapping("/api/v1/article/updateById")
    ResponseResult updateArticle(@RequestBody ApArticle article);
}
