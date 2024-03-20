package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMapper newsMapper;

    /**
     * 查询文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        //1.检查参数
        dto.checkParam();
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
        queryWrapper.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());
        //按照发布时间倒叙查询
        queryWrapper.orderByDesc(WmNews::getPublishTime);
        page = newsMapper.selectPage(page, queryWrapper);
        //3.返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page);
        return responseResult;
    }

}