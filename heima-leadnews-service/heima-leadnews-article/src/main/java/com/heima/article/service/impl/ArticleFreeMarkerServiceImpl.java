package com.heima.article.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreeMarkerService;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.beans.ApArticleContent;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ArticleFreeMarkerServiceImpl implements ArticleFreeMarkerService {
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleMapper articleMapper;

    /**
     * 生成静态文件 上传到minio中
     *
     * @param apArticle
     * @param content
     */
    @Override
    @Async
    public void buildArticleToMinIO(ApArticle apArticle, String content) throws IOException, TemplateException {
        if (StringUtils.isNotBlank(content)) {
            //通过freemarker生成html文件
            //获取模板
            Template template = configuration.getTemplate("article.ftl");
            //准备数据
            Map<String, Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(content));
            StringWriter out = new StringWriter();
            //生成html文件
            template.process(map, out);
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            //把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);
            //修改ap_article表的static_url字段
            LambdaQueryWrapper<ApArticle> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApArticle::getId, apArticle.getId());
            ApArticle dbApArticle = articleMapper.selectOne(wrapper);
            dbApArticle.setStaticUrl(path);
            articleMapper.updateById(dbApArticle);
        } else {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
    }
}
