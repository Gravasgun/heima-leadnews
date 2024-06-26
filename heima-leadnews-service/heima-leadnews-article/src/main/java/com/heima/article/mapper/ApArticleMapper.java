package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.ArticleHomeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * 加载文章列表
     * @param dto
     * @param type 1：加载更多 2：加载最新
     * @return
     */
     List<ApArticle> loadArticleList(ArticleHomeDto dto, short type);
     List<ApArticle> findArticleListInFiveDays(@Param("dayParam") Date dayParam);
}
