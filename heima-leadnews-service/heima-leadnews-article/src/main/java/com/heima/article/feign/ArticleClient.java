package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApCollectionService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService articleService;
    @Autowired
    private ApCollectionService collectionService;

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
}
