package com.heima.article.listener;

import com.alibaba.fastjson.JSONObject;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constans.WmNewsMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ArticleListener {
    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void OnMessage(String message) {
        if (StringUtils.isNotBlank(message)) {
            Map map = JSONObject.parseObject(message, Map.class);
            apArticleConfigService.updateByMap(map);
        }
    }
}
