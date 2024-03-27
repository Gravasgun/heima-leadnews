package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.LocalImageScanUtil;
import com.heima.common.aliyun.TextScanUtil;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private TextScanUtil textScanUtil;

    @Autowired
    private LocalImageScanUtil localImageScanUtil;

    @Autowired
    private IArticleClient iArticleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    /**
     * 自媒体文章审核
     *
     * @param articleId 文章id
     */
    @Override
    public void autoScanNews(Integer articleId) {
        //1.查询自媒体文章
        WmNews news = wmNewsMapper.selectById(articleId);
        if (news == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //处于待审核状态的文章
        if (news.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            try {
                //2.审核文本内容
                Map<String, Object> textAndImages = getTextAndImages(news);
                String text = (String) textAndImages.get("text");
                boolean textScanResult = handleTextScan(text, news);
                if (!textScanResult) {
                    return;
                }
                //获取图片内容
                Set<String> images = (Set) textAndImages.get("images");
                //3.审核图片内容
                boolean imagesScanResult = handleImagesScan(images, news);
                if (!imagesScanResult) {
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return;
        }
        //4.审核成功，保存app端相关数据
        ResponseResult responseResult = saveAppArticle(news);
        if (!responseResult.getCode().equals(200)) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核-保存app端相关文章数据失败");
        }
        //回填文章id
        news.setArticleId((Long) responseResult.getData());
        news.setStatus((short) 9);
        news.setReason("审核成功");
        //修改文章信息
        wmNewsMapper.updateById(news);
    }

    /**
     * 保存app端文章
     *
     * @param news
     */
    public ResponseResult saveAppArticle(WmNews news) {
        ArticleDto articleDto = new ArticleDto();
        //属性拷贝
        BeanUtils.copyProperties(news, articleDto);
        //文章布局
        articleDto.setLayout(news.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(news.getChannelId());
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }
        //作者
        articleDto.setAuthorId(news.getUserId().longValue());
        WmUser user = wmUserMapper.selectById(news.getUserId());
        if (user != null) {
            articleDto.setAuthorName(user.getName());
        }
        //文章id
        if (news.getArticleId() != null) {
            articleDto.setId(news.getArticleId());
        }
        //创建时间
        articleDto.setCreatedTime(new Date());
        return iArticleClient.saveArticle(articleDto);
    }

    /**
     * 审核图片内容
     *
     * @param images
     * @param news
     * @return
     */
    private boolean handleImagesScan(Set<String> images, WmNews news) {
        boolean result = true;
        if (images != null && images.isEmpty()) {
            return result;
        }
        try {
            for (String image : images) {
                Map imageScanMap = localImageScanUtil.localImageScan(image);
                if (imageScanMap.get("suggestion").equals("block")) {
                    result = false;
                    news.setStatus((short) 2);
                    news.setReason("当前文章的图片中存在违规内容");
                    wmNewsMapper.updateById(news);
                    break;
                }
            }
        } catch (Exception e) {
            result = false;
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 审核纯文本内容
     *
     * @param content
     * @param news
     * @return
     */
    public boolean handleTextScan(String content, WmNews news) {
        boolean result = true;
        if (content != null && (news.getTitle() + "" + content).length() == 0) {
            return result;
        }
        try {
            Map<String, String> map = textScanUtil.greeTextScan(content + "-" + news.getTitle());
            //审核失败
            if (map != null && map.get("suggestion").equals("block")) {
                result = false;
                news.setStatus((short) 2);
                news.setReason("当前文章中存在违规内容");
                wmNewsMapper.updateById(news);
            }
        } catch (Exception e) {
            result = false;
            throw new RuntimeException(e);
        }
        return result;
    }


    /**
     * 1.从自媒体文章的content获取文本和图片
     * 2.获取文章的封面图片
     *
     * @param news
     * @return
     */
    private Map<String, Object> getTextAndImages(WmNews news) {
        //存储文本和图片
        Map<String, Object> resultMap = new HashMap<>();
        //存储文本
        StringBuilder stringBuilder = new StringBuilder();
        //存储图片
        Set<String> set = new HashSet<>();
        //判空
        if (StringUtils.isBlank(news.getContent()) && StringUtils.isBlank(news.getImages())) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //1.从自媒体文章的content获取文本和图片
        if (StringUtils.isNotBlank(news.getContent())) {
            List<Map> maps = JSONArray.parseArray(news.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("image")) {
                    set.add((String) map.get("value"));
                }
            }
        }

        //2.获取文章的封面图片
        if (StringUtils.isNotBlank(news.getImages())) {
            String[] split = news.getImages().split(",");
            set.addAll(Arrays.asList(split));
        }
        resultMap.put("text", stringBuilder.toString());
        resultMap.put("images", set);
        return resultMap;
    }
}