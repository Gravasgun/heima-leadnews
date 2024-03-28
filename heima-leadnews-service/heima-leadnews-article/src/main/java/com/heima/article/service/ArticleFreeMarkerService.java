package com.heima.article.service;

import com.heima.model.article.beans.ApArticle;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface ArticleFreeMarkerService {
    /**
     * 生成静态文件 上传到minio中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinIO(ApArticle apArticle,String content) throws IOException, TemplateException;
}
