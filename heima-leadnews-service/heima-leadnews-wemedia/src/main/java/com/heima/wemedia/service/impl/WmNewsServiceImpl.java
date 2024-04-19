package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IArticleClient;
import com.heima.common.constans.WemediaConstants;
import com.heima.common.constans.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dtos.AdNewsAuthDto;
import com.heima.model.admin.dtos.AdNewsDto;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMapper newsMapper;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmMaterialMapper materialMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmNewsTaskService newsTaskService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private WmUserService userService;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmChannelService channelService;

    /**
     * 查询文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        JSON.toJSONString(dto);
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        //获取当前登录人的信息
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //2.分页查询
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        //状态精确查询
        if (dto.getStatus() != null) {
            queryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        //频道精确查询
        if (dto.getChannelId() != null) {
            queryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            queryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }
        //关键字模糊查询
        if (StringUtils.isNotBlank(dto.getKeyword())) {
            queryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }
        //查询当前登陆人的文章
        queryWrapper.eq(WmNews::getUserId, user.getId());
        //按照发布时间倒叙查询
        queryWrapper.orderByDesc(WmNews::getPublishTime);
        page = newsMapper.selectPage(page, queryWrapper);
        //3.返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 发布文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //1.保存或修改文章
        WmNews news = new WmNews();
        //属性拷贝 -> 只有类型和名称相同的属性才能拷贝
        BeanUtils.copyProperties(dto, news);
        //手动拷贝
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StringUtils.join(dto.getImages(), ",");
            news.setImages(imageStr);
        }
        //如果当前封面类型是自动 -> -1,数据库中type字段不能为负数
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            news.setType(null);
        }
        saveOrUpdateWmNews(news);
        //2.判断是否为草稿，如果为草稿，结束该方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //3.不是草稿，保存文章内容图片与素材的关系
        //获取文章内容的图片信息
        List<String> pictureUrlList = getPictureUrlInfo(dto.getContent());
        saveRelativeInfo(pictureUrlList, news.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        //4.不是草稿，保存文章封面图片与素材的关系
        saveRelativeInfoForCover(dto, news, pictureUrlList);
        //5.异步提交文章审核
        //wmNewsAutoScanService.autoScanNews(news.getId());
        //6.集成任务调度微服务
        newsTaskService.addNewsToTask(news.getId(), news.getPublishTime());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 功能一：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1.如果内容图片大于等于1，小于3，单图，type=1
     * 2.如果内容图片大于等于3，多图，type=3
     * 3.如果内容图片为0，无图，type=0
     * <p>
     * 功能二：保存封面图片与素材的关系
     *
     * @param dto
     * @param news
     * @param pictureUrlList
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews news, List<String> pictureUrlList) {
        List<String> imageList = dto.getImages();
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //无图
            if (pictureUrlList == null || pictureUrlList.size() == 0) {
                news.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            //单图
            if (pictureUrlList != null && pictureUrlList.size() >= 1 && pictureUrlList.size() < 3) {
                news.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                imageList = pictureUrlList.stream().limit(1).collect(Collectors.toList());
            }
            //多图
            if (pictureUrlList != null && pictureUrlList.size() >= 3) {
                news.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                imageList = pictureUrlList.stream().limit(3).collect(Collectors.toList());
            }
        }
        //修改文章
        if (imageList != null && imageList.size() > 0) {
            String image = StringUtils.join(imageList, ",");
            news.setImages(image);
        }
        updateById(news);
        //保存封面图片与素材的关系
        if (imageList != null && imageList.size() > 0) {
            saveRelativeInfo(imageList, news.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }


    /**
     * 保存文章图片(内容图片或封面图片)与素材关系到数据库中
     *
     * @param pictureUrlList
     * @param newsId
     * @param type           0：内容图片引用 1：封面图片引用
     */
    private void saveRelativeInfo(List<String> pictureUrlList, Integer newsId, Short type) {
        if (pictureUrlList != null && !pictureUrlList.isEmpty()) {
            //通过图片的url查询素材id
            LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(WmMaterial::getUrl, pictureUrlList);
            List<WmMaterial> materialList = materialMapper.selectList(queryWrapper);
            if (materialList == null || materialList.size() == 0) {
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }
            if (materialList.size() != pictureUrlList.size()) {
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }
            //List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
            List<Integer> idList = materialList.stream().mapToInt(WmMaterial::getId).boxed().collect(Collectors.toList());
            //批量保存
            wmNewsMaterialMapper.saveRelations(idList, newsId, type);
        }

    }

    /**
     * 提取文章内容中的图片信息
     *
     * @param content
     * @return
     */
    private List<String> getPictureUrlInfo(String content) {
        List<String> imageList = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String imgStr = (String) map.get("value");
                imageList.add(imgStr);
            }
        }
        return imageList;
    }


    /**
     * 保存或修改文章
     *
     * @param news
     */
    private void saveOrUpdateWmNews(WmNews news) {
        //补全属性
        news.setUserId(WmThreadLocalUtil.getUser().getId());
        news.setCreatedTime(new Date());
        news.setSubmitedTime(new Date());
        news.setEnable((short) 1);//默认上架
        if (news.getId() == null) {
            //保存
            save(news);
        } else {
            //修改(先删除关系 后修改文章)
            LambdaQueryWrapper<WmNewsMaterial> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WmNewsMaterial::getNewsId, news.getId());
            wmNewsMaterialMapper.delete(queryWrapper);
            updateById(news);
        }
    }

    /**
     * 文章上下架
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        //1.校验参数
        if (dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询文章
        WmNews news = newsMapper.selectById(dto.getId());
        if (news == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.判断文章是否已发布
        if (!news.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前文章不是发布状态，不能上下架");
        }
        //4.修改文章enable
        if (dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2) {
            news.setEnable(dto.getEnable());
            updateById(news);
            //发送消息，通知article微服务修改文章配置
            if (news.getArticleId() != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("articleId", news.getArticleId());
                map.put("enable", dto.getEnable());
                kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSONObject.toJSONString(map));
            }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 管理端查询文章列表
     *
     * @param dto
     * @return
     */
    public ResponseResult listVo(AdNewsAuthDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 2 分页查询
        IPage<WmNews> pageCheck = new Page(dto.getPage(), dto.getSize());
        // 3 按照不同需求查询
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //3.1 状态
        if (dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        //模糊查询
        if (StringUtils.isNotBlank(dto.getTitle())) {
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getTitle());
        }
        //3.2 排序
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        pageCheck = page(pageCheck, lambdaQueryWrapper);
        //4. 返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) pageCheck.getTotal());
        List<WmNews> wmNewsList = pageCheck.getRecords();
        List<AdNewsDto> list = new ArrayList<>();
        for (WmNews wmNews : wmNewsList) {
            WmUser user = userService.getById(wmNews.getUserId());
            String name = user.getName();
            AdNewsDto adNewsDto = new AdNewsDto();
            BeanUtils.copyProperties(wmNews, adNewsDto);
            adNewsDto.setAuthorName(name);
            adNewsDto.setUserId(null);
            list.add(adNewsDto);
        }
        responseResult.setData(list);
        return responseResult;
    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult adminFindOneNews(Integer id) {
        //参数校验
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询文章
        WmNews news = newsMapper.selectById(id);
        //查询用户
        WmUser user = userService.getById(news.getUserId());
        String name = user.getName();
        AdNewsDto adNewsDto = new AdNewsDto();
        BeanUtils.copyProperties(news, adNewsDto);
        adNewsDto.setAuthorName(name);
        ResponseResult responseResult = new ResponseResult<>();
        responseResult.setData(adNewsDto);
        return responseResult;
    }

    /**
     * 文章审核失败
     *
     * @param authDto
     * @return
     */
    @Override
    public ResponseResult adminNewsAuthFail(AdNewsAuthDto authDto) {
        //参数校验
        if (authDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews news = getById(authDto.getId());
        if (StringUtils.isNotBlank(authDto.getMsg())) {
            news.setReason(authDto.getMsg());
        }
        news.setStatus(Short.parseShort(authDto.getStatus().toString()));
        updateById(news);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 文章审核通过
     *
     * @param authDto
     * @return
     */
    @Override
    public ResponseResult adminNewsAuthPass(AdNewsAuthDto authDto) {
        //参数校验
        if (authDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews news = getById(authDto.getId());
        //属性赋值
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(news, articleDto);
        articleDto.setAuthorId(Long.parseLong(news.getUserId().toString()));
        articleDto.setAuthorName(userService.getById(news.getUserId()).getName());
        articleDto.setChannelName(channelService.getById(news.getChannelId()).getName());
        articleDto.setLayout(news.getType());
        //需要创建app端的文章信息
        ResponseResult responseResult = articleClient.saveArticle(articleDto);
        //更新自媒体文章
        //回填文章id
        news.setArticleId((Long) responseResult.getData());
        news.setStatus(Short.parseShort(authDto.getStatus().toString()));
        news.setReason("人工审核成功");
        //修改文章信息
        updateById(news);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}