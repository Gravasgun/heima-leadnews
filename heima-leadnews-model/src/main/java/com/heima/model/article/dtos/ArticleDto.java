package com.heima.model.article.dtos;

import com.heima.model.article.beans.ApArticle;
import lombok.Data;

@Data
public class ArticleDto extends ApArticle {
    //文章内容
    private String content;
}
