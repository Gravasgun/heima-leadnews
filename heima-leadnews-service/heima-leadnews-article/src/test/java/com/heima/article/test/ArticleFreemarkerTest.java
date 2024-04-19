package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
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
import java.util.List;
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

    @Autowired
    private ApArticleService articleService;

    /**
     * 手动上传所有文章的静态路径
     *
     * @throws IOException
     * @throws TemplateException
     */
    @Test
    public void createStaticUrlTest() throws IOException, TemplateException {
        //1.获取所有文章id
        List<ApArticle> apArticles = articleService.list();
        for (ApArticle article : apArticles) {
            //2.获取每个文章的内容
            ApArticleContent apArticleContent = apArticleContentMapper
                    .selectOne(Wrappers
                            .<ApArticleContent>lambdaQuery()
                            .eq(ApArticleContent::getArticleId, article.getId()));
            if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
                //3.文章内容通过freemarker生成静态html页面
                Template template = configuration.getTemplate("article.ftl");
                //3.1 创建模型
                Map<String, Object> content = new HashMap();
                content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
                //3.2 输出流
                StringWriter writer = new StringWriter();
                //3.3 合成方法
                template.process(content, writer);
                //4.把静态页面上传到minio
                //4.1 文件流
                InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());
                String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", inputStream);
                //5.把静态页面的路径保存到数据库
                articleService.update(Wrappers
                        .<ApArticle>lambdaUpdate()
                        .eq(ApArticle::getId, apArticleContent.getArticleId())
                        .set(ApArticle::getStaticUrl, path));
            }
        }
//        //查询所有的文章内容
//        List<ApArticleContent> articleContentsList = apArticleContentMapper.selectList(null);
//        //通过循环一个个的上传文章详情
//        for (int i = 0; i < articleContentsList.size(); i++) {
//            if (articleContentsList.get(i) != null && StringUtils.isNotBlank(articleContentsList.get(i).getContent())) {
//                //通过freemarker生成html文件
//                //获取模板
//                Template template = configuration.getTemplate("article.ftl");
//                //准备数据
//                Map<String, Object> content = new HashMap<>();
//                content.put("content", JSONArray.parseArray(articleContentsList.get(i).getContent()));
//                StringWriter out = new StringWriter();
//                //生成html文件
//                template.process(content, out);
//                InputStream in = new ByteArrayInputStream(out.toString().getBytes());
//                //把html文件上传到minio中
//                String path = fileStorageService.uploadHtmlFile("", articleContentsList.get(i).getArticleId() + ".html", in);
//                //修改ap_article表的static_url字段
//                LambdaQueryWrapper<ApArticle> wrapper = new LambdaQueryWrapper<>();
//                wrapper.eq(ApArticle::getId, articleContentsList.get(i).getArticleId());
//                ApArticle apArticle = articleMapper.selectOne(wrapper);
//                apArticle.setStaticUrl(path);
//                articleMapper.updateById(apArticle);
//            }
//        }
    }
}
