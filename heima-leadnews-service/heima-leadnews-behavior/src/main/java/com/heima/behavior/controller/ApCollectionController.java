package com.heima.behavior.controller;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/collection_behavior")
public class ApCollectionController {

    @Autowired
    private IArticleClient articleClient;


    @PostMapping
    public ResponseResult collection(@RequestBody CollectionBehaviorDto dto) {
        return articleClient.collectionArticle(dto);
    }
}
