package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.beans.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {
    @Autowired
    private ApArticleConfigMapper configMapper;

    /**
     * 修改文章配置
     *
     * @param map
     */
    @Override
    public void updateByMap(Map map) {
        String articleId = String.valueOf(map.get("articleId"));
        //根据articleId查询文章配置
        LambdaQueryWrapper<ApArticleConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApArticleConfig::getArticleId, articleId);
        ApArticleConfig apArticleConfig = configMapper.selectOne(queryWrapper);
        //修改文章配置
        Integer enable = Integer.parseInt(String.valueOf(map.get("enable")));
        Boolean isDown = true;
        if (enable.equals(1)) {
            isDown = false;
        }
        apArticleConfig.setIsDown(isDown);
        configMapper.updateById(apArticleConfig);
    }
}
