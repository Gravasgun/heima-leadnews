package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

public interface TaskService {
    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    long addTask(Task task);

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    boolean cancelTask(Long taskId);

    /**
     * 按照任务类型和优先级拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    Task pollTask(int type, int priority);

    /**
     * 未来数据定时刷新
     */
    void refresh();

    void hotArticleTimedCalculate();
}
