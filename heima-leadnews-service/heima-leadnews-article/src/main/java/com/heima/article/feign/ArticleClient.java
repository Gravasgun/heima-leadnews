package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApCollectionService;
import com.heima.article.service.HotArticleService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService articleService;
    @Autowired
    private ApCollectionService collectionService;
    @Autowired
    private HotArticleService hotArticleService;

    /**
     * 保存文章
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return articleService.saveArticle(dto);
    }

    /**
     * 收藏文章
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/collection_behavior")
    public ResponseResult collectionArticle(@RequestBody CollectionBehaviorDto dto) {
        return collectionService.collection(dto);
    }

    /**
     * 加载文章行为-数据回显
     *
     * @param dto
     * @return
     */
    @Override
    @PostMapping("/api/v1/article/load_article_behavior/")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto dto) {
        return articleService.loadArticleBehavior(dto);
    }

    /**
     * 热点文章定时计算
     */
    @Override
    @PostMapping("/api/v1/article/calculateHotArticle")
    public ResponseResult calculateHotArticle() {
        hotArticleService.calculateHotArticle();
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 根据文章id查询文章
     *
     * @param id 文章id
     * @return
     */
    @GetMapping("/api/v1/article/{id}")
    public ResponseResult findArticleById(@PathVariable("id") Long id) {
        return articleService.findArticleById(id);
    }

    /**
     * 根据文章id更新文章
     *
     * @param article
     * @return
     */
    @Override
    @PostMapping("/api/v1/article/updateById")
    public ResponseResult updateArticle(@RequestBody ApArticle article) {
        return articleService.updateArticle(article);
    }
}
