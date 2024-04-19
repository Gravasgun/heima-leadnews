package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class LikesBehaviorDto {
    /**
     * 文章id
     */
    Long articleId;

    /**
     * 0文章
     * 1动态
     * 2评论
     */
    Short type;

    /**
     * 0 点赞
     * 1 取消点赞
     */
    Short operation;
}