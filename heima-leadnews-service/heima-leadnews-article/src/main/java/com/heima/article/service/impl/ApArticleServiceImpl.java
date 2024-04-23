package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApArticleFreeMarkerService;
import com.heima.common.constans.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.beans.ApArticle;
import com.heima.model.article.beans.ApArticleConfig;
import com.heima.model.article.beans.ApArticleContent;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.beans.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.heima.common.constans.ArticleConstants.*;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper articleMapper;

    private final static short MAX_PAGE_SIZE = 50;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleFreeMarkerService apArticleFreeMarkerService;

    @Autowired
    private CacheService cacheService;

    /**
     * 查询文章列表
     *
     * @param dto
     * @param type 1：加载更多 2：加载最新
     * @return
     */
    @Override

    public ResponseResult load(ArticleHomeDto dto, Short type) {
        //分页条数校验
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        //校验参数type
        if (!type.equals(LOADTYPE_LOAD_MORE) && !type.equals(LOADTYPE_LOAD_NEW)) {
            type = LOADTYPE_LOAD_MORE;
        }
        //校验参数tag
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(DEFAULT_TAG);
        }
        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        //查询
        List<ApArticle> articleList = articleMapper.loadArticleList(dto, type);
        //返回结果
        return ResponseResult.okResult(articleList);
    }


    /**
     * 保存app端相关文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
//        //服务降级处理测试
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        JSONObject.toJSONString(dto);
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);
        ApArticleContent apArticleContent = null;
        //2.判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容
            //保存文章
            save(apArticle);
            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);
            //保存文章内容
            apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            //2.2 存在id   修改  文章  文章内容
            //修改文章
            updateById(apArticle);
            //修改文章内容
            LambdaQueryWrapper<ApArticleContent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ApArticleContent::getArticleId, dto.getId());
            apArticleContent = apArticleContentMapper.selectOne(queryWrapper);
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }
        //异步调用 生成静态文件并上传到minio中
        try {
            apArticleFreeMarkerService.buildArticleToMinIO(apArticle, apArticleContent.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 加载文章详情 数据回显
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        //参数校验
        if (dto == null || dto.getArticleId() == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //{ "isFollow": true, "isLike": true,"isUnlike": false,"isCollection": true }
        boolean isFollow = false, isLike = false, isUnlike = false, isCollection = false;
        ApUser user = AppThreadLocalUtil.getUser();
        //判断是否登录
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //喜欢行为
        String likeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if (StringUtils.isNotBlank(likeBehaviorJson)) {
            isLike = true;
        }
        //不喜欢的行为
        String unLikeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if (StringUtils.isNotBlank(unLikeBehaviorJson)) {
            isUnlike = true;
        }
        //是否收藏
        String collctionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getArticleId().toString());
        if (StringUtils.isNotBlank(collctionJson)) {
            isCollection = true;
        }
        //是否关注
        Double score = cacheService.zScore(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
        if (score != null) {
            isFollow = true;
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isfollow", isFollow);
        resultMap.put("islike", isLike);
        resultMap.put("isunlike", isUnlike);
        resultMap.put("iscollection", isCollection);
        return ResponseResult.okResult(resultMap);
    }

    /**
     * 查询文章列表(热点数据)
     *
     * @param dto
     * @param type      1：加载更多 2：加载最新
     * @param firstPage true:是首页 false:非首页
     * @return
     */
    @Override
    public ResponseResult loadHotArticle(ArticleHomeDto dto, Short type, Boolean firstPage) {
        if (firstPage) {
            String jsonStr = cacheService.get(HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StringUtils.isNotBlank(jsonStr)) {
                List<HotArticleVo> articleVoList = JSONObject.parseArray(jsonStr, HotArticleVo.class);
                return ResponseResult.okResult(articleVoList);
            }
        }
        return load(dto, type);
    }
}