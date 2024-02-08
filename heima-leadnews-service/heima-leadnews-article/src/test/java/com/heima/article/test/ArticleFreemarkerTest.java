package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.beans.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleMapper articleMapper;

    @Test
    public void createStaticUrlTest() throws IOException, TemplateException {
        //获取文章内容
        LambdaQueryWrapper<ApArticleContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApArticleContent::getArticleId, "1302862387124125698L");
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            //通过freemarker生成html文件
            //获取模板
            Template template = configuration.getTemplate("article.ftl");
            //准备数据
            Map<String, Object> content = new HashMap<>();
            content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            StringWriter out = new StringWriter();
            //生成html文件
            template.process(content, out);
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            //把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);
            //修改ap_article表的static_url字段
            LambdaQueryWrapper<ApArticle> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApArticle::getId, apArticleContent.getArticleId());
            ApArticle apArticle = articleMapper.selectOne(wrapper);
            apArticle.setStaticUrl(path);
            articleMapper.updateById(apArticle);
        }
    }
}
