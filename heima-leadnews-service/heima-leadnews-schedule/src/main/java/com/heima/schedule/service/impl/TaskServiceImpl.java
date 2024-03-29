package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.common.constans.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.beans.TaskInfo;
import com.heima.model.schedule.beans.TaskInfoLogs;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.mapper.TaskInfoLogsMapper;
import com.heima.schedule.mapper.TaskInfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private TaskInfoLogsMapper taskInfoLogsMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中
        boolean flag = addTaskToDataBase(task);
        if (flag) {
            //2.添加任务到redis中
            addTaskToRedis(task);
        }
        return task.getTaskId();
    }

    /**
     * 添加任务到Redis中
     *
     * @param task
     */
    private void addTaskToRedis(Task task) {
        //如果任务的执行时间小于等于当前时间，添加到list中
        String key = task.getTaskType() + "_" + task.getPriority();
        //获取预设时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long futureTime = calendar.getTimeInMillis();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.NOW + key, JSONObject.toJSONString(task));
        } else if (task.getExecuteTime() <= futureTime) {
            //如果任务的执行时间大于当前时间，小于等于预设时间(未来5分钟)，存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSONObject.toJSONString(task), task.getExecuteTime());
        }
    }

    /**
     * 添加任务到数据库中
     *
     * @param task
     * @return
     */
    private boolean addTaskToDataBase(Task task) {
        boolean flag = false;
        try {
            //新增TaskInfo
            TaskInfo taskInfo = new TaskInfo();
            //属性复制
            BeanUtils.copyProperties(task, taskInfo);
            //设置执行时间
            taskInfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskInfoMapper.insert(taskInfo);

            //设置taskId
            task.setTaskId(taskInfo.getTaskId());

            //新增TaskInfoLogs
            TaskInfoLogs taskInfoLogs = new TaskInfoLogs();
            //属性复制
            BeanUtils.copyProperties(taskInfo, taskInfoLogs);
            //设置状态
            taskInfoLogs.setStatus(ScheduleConstants.INIT);
            //设置初始版本号
            taskInfoLogs.setVersion(1);
            taskInfoLogsMapper.insert(taskInfoLogs);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
