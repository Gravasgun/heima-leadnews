package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@Transactional
public class WmNewsTaskServiceImpl implements WmNewsTaskService {
    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private WmNewsAutoScanService newsAutoScanService;

    /**
     * 添加任务到延迟队列中
     *
     * @param id          文章的id
     * @param publishTime 文章的发布时间 可以作为任务的执行时间
     */
    @Override
    @Async
    public void addNewsToTask(Integer id, Date publishTime) {
        log.info("添加任务到任务调度服务中------------开始");
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews news = new WmNews();
        news.setId(id);
        task.setParameters(ProtostuffUtil.serialize(news));
        scheduleClient.addTask(task);
        log.info("添加任务到任务调度服务中------------结束");
    }

    /**
     * 消费任务 审核文章
     */
    @Scheduled(fixedRate = 1000)
    @Override
    public void consumeTask() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //log.info(simpleDateFormat.format(new Date()) + "消费任务，审核文章");
        ResponseResult responseResult = scheduleClient.pollTask(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (responseResult != null && responseResult.getCode().equals(200) && responseResult.getData() != null) {
            Task task = JSONObject.parseObject(JSONObject.toJSONString(responseResult.getData()), Task.class);
            WmNews news = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
            newsAutoScanService.autoScanNews(news.getId());
        }
    }
}
