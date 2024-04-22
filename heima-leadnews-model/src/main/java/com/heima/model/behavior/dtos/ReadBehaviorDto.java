package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class ReadBehaviorDto {

    /**
     * 文章ID
     */
    Long articleId;

    /**
     * 阅读次数
     */
    Short count;
}