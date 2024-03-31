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
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedissonClient redissonClient;

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
     * 1.添加任务到数据库中
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

    /**
     * 2.添加任务到Redis中
     *
     * @param task
     */
    private void addTaskToRedis(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        //获取预设时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long futureTime = calendar.getTimeInMillis();
        //如果任务的执行时间小于等于当前时间，添加到list中
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.NOW + key, JSONObject.toJSONString(task));
        } else if (task.getExecuteTime() <= futureTime) {
            //如果任务的执行时间大于当前时间，小于等于预设时间(未来5分钟)，存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSONObject.toJSONString(task), task.getExecuteTime());
        }
    }

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @Override
    public boolean cancelTask(Long taskId) {
        boolean flag = false;
        //1.删除任务 更新任务日志
        Task task = updateDataBase(taskId, ScheduleConstants.CANCELLED);
        //2.删除redis中的缓存
        if (task != null) {
            removeTaskFromRedis(task);
            flag = true;
        }
        return flag;
    }

    /**
     * 1.删除任务 更新任务日志
     *
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDataBase(Long taskId, int status) {
        Task task = null;
        try {
            //1.删除任务
            taskInfoMapper.deleteById(taskId);
            //2.更新任务日志
            TaskInfoLogs taskInfoLogs = taskInfoLogsMapper.selectById(taskId);
            taskInfoLogs.setStatus(status);
            taskInfoLogsMapper.updateById(taskInfoLogs);
            task = new Task();
            BeanUtils.copyProperties(taskInfoLogs, task);
            task.setExecuteTime(taskInfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel error taskId={}", taskId);
            throw new RuntimeException("task cancel error taskId={}");
        }
        return task;
    }

    /**
     * 2.删除redis中的Task缓存
     *
     * @param task
     */
    private void removeTaskFromRedis(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.NOW + key, 0, JSONObject.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSONObject.toJSONString(task));
        }
    }

    /**
     * 按照任务类型和优先级拉取任务(消费list中的任务)
     *
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task pollTask(int type, int priority) {
        Task task = null;
        try {
            String key = type + "_" + priority;
            //从redis中拉取数据
            String taskJson = cacheService.lRightPop(ScheduleConstants.NOW + key);
            if (StringUtils.isNotBlank(taskJson)) {
                task = JSONObject.parseObject(taskJson, Task.class);
                //修改数据库信息
                updateDataBase(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }
        return task;
    }

    /**
     * 未来任务定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {
        RLock lock = redissonClient.getLock("FUTURE_TASK_SYNC");
        try {
            boolean flag = lock.tryLock(1, 30, TimeUnit.SECONDS);
            if (flag) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(simpleDateFormat.format(new Date()) + "执行了定时任务");
                //获取zset中的所有未执行任务的key
                Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
                for (String futureKey : futureKeys) {
                    //futureKeys中的每一个key都是一个set集合，把这个set集合中的任务按照score，把执行时间小于当前时间的任务取出来
                    Set<String> futureTask = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                    if (!futureTask.isEmpty()) {
                        String topicKey = ScheduleConstants.NOW + futureKey.split(ScheduleConstants.FUTURE)[1];
                        //使用redis管道技术将未来任务从zset中删除、添加进list中
                        cacheService.refreshWithPipeline(futureKey, topicKey, futureTask);
                        System.out.println("成功的将" + futureKey + "下的当前需要执行的任务数据刷新到" + topicKey + "下");
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}