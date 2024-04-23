package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApCollectionService;
import com.heima.common.constans.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.beans.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApArticleService articleService;

    /**
     * 用户收藏文章功能
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        //参数校验
        if (dto == null || dto.getEntryId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //查询
        String collectionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString());
        if (StringUtils.isNotBlank(collectionJson) && dto.getOperation() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已收藏");
        }
        //收藏
        if (dto.getOperation() == 0) {
            log.info("文章收藏，保存key:{},{},{}", dto.getEntryId(), user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hPut(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString(), JSON.toJSONString(dto));
            //查询文章，修改收藏量
            ResponseResult responseResult = articleService.findArticleById(dto.getEntryId());
            if (responseResult != null && responseResult.getCode().equals(200)) {
                ApArticle article = JSONObject.parseObject(JSONObject.toJSONString(responseResult.getData()), ApArticle.class);
                if (article.getCollection() == null) {
                    article.setCollection(1);
                } else {
                    article.setCollection(article.getCollection() + 1);
                }
                //更新文章数据
                articleService.updateArticle(article);
            }
        } else {
            //取消收藏
            log.info("文章收藏，删除key:{},{},{}", dto.getEntryId(), user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hDelete(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString());
            //查询文章，修改收藏量
            ResponseResult responseResult = articleService.findArticleById(dto.getEntryId());
            if (responseResult != null && responseResult.getCode().equals(200)) {
                ApArticle article = JSONObject.parseObject(JSONObject.toJSONString(responseResult.getData()), ApArticle.class);
                article.setCollection(article.getCollection() - 1);
                //更新文章数据
                articleService.updateArticle(article);
            }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
