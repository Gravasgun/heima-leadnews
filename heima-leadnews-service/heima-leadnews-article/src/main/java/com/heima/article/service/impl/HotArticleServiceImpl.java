package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.redis.CacheService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.heima.common.constans.ArticleConstants.*;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {
    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;

    /**
     * 计算热点文章
     */
    @Override
    public void calculateHotArticle() {
        //1.查询前5天的数据
        Date dateParam = DateTime.now().minusDays(5).toDate();
        List<ApArticle> articleList = articleMapper.findArticleListInFiveDays(dateParam);
        //2.计算文章分值
        List<HotArticleVo> articleVoList = calculateHotArticleScore(articleList);
        //3.为每个频道缓存30条分值较高的文章
        cacheArticleToRedis(articleVoList);
    }

    /**
     * 为每个频道缓存30条分值较高的文章
     *
     * @param articleVoList
     */
    private void cacheArticleToRedis(List<HotArticleVo> articleVoList) {
        //查询所有频道
        ResponseResult responseResult = wemediaClient.findList();
        if (responseResult.getCode().equals(200)) {
            List<WmChannel> channelList = JSONObject.parseArray(JSONObject.toJSONString(responseResult.getData()), WmChannel.class);
            if (channelList != null && !channelList.isEmpty()) {
                for (WmChannel channel : channelList) {
                    //检索出每个频道的文章
                    List<HotArticleVo> list = articleVoList.stream().filter(article -> article.getChannelId().equals(channel.getId())).collect(Collectors.toList());
                    //文章排序，取30条存入redis
                    //为每个频道设置推荐数据 key：频道id value：文章
                    sortAndCache(list, channel.getId().toString());
                }
            }
        }
        //为首页设置推荐数据(不分频道，只取前30)
        sortAndCache(articleVoList, DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     *
     * @param articleVoList
     * @param key
     */
    private void sortAndCache(List<HotArticleVo> articleVoList, String key) {
        if (articleVoList != null && !articleVoList.isEmpty()) {
            articleVoList = articleVoList.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
            if (articleVoList.size() > 30) {
                articleVoList = articleVoList.subList(0, 30);
            }
            cacheService.set(HOT_ARTICLE_FIRST_PAGE + key, JSONObject.toJSONString(articleVoList), 5l, TimeUnit.DAYS);
        }
    }

    /**
     * 计算文章分值
     *
     * @param articleList
     * @return
     */
    private List<HotArticleVo> calculateHotArticleScore(List<ApArticle> articleList) {
        List<HotArticleVo> list = new ArrayList<>();
        if (articleList != null && !articleList.isEmpty()) {
            for (ApArticle article : articleList) {
                HotArticleVo hotArticleVo = new HotArticleVo();
                BeanUtils.copyProperties(article, hotArticleVo);
                Integer score = calculateScore(article);
                hotArticleVo.setScore(score);
                list.add(hotArticleVo);
            }
        }
        return list;
    }

    /**
     * 计算文章具体分值
     *
     * @param article
     * @return
     */
    private Integer calculateScore(ApArticle article) {
        Integer score = 0;
        if (article.getLikes() != null) {
            score += article.getLikes() * HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (article.getCollection() != null) {
            score += article.getCollection() * HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        if (article.getViews() != null) {
            score += article.getViews();
        }
        if (article.getComment() != null) {
            score += article.getComment() * HOT_ARTICLE_COMMENT_WEIGHT;
        }
        return score;
    }
}
