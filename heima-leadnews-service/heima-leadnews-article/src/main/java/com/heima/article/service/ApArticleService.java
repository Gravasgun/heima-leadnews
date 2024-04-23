package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleService extends IService<ApArticle> {
    /**
     * 查询文章列表
     *
     * @param dto
     * @param type 1：加载更多 2：加载最新
     * @return
     */
    ResponseResult load(ArticleHomeDto dto, Short type);

    /**
     * 查询文章列表(热点数据)
     *
     * @param dto
     * @param type      1：加载更多 2：加载最新
     * @param firstPage true:是首页 false:非首页
     * @return
     */
    ResponseResult loadHotArticle(ArticleHomeDto dto, Short type, Boolean firstPage);

    /**
     * 保存app端相关文章
     *
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);

    /**
     * 加载文章详情 数据回显
     *
     * @param dto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto dto);

    /**
     * 根据文章id查询文章
     *
     * @param id 文章id
     * @return
     */
    ResponseResult findArticleById(Long id);

    /**
     * 根据文章id更新文章
     * @param article
     * @return
     */
    ResponseResult updateArticle(ApArticle article);
}
