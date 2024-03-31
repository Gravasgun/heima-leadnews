package com.heima.schedule.test;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class ScheduleTest {
    @Autowired
    private TaskService taskService;

    @Test
    public void testAddTask() {
//        Task task = new Task();
//        task.setTaskType(100);
//        task.setPriority(50);
//        task.setParameters("新增task测试".getBytes());
//        //测试未来任务
//        task.setExecuteTime(new Date().getTime() + 500);
//        //测试当前任务
//        //task.setExecuteTime(new Date().getTime());
//        long taskId = taskService.addTask(task);
//        System.out.println(taskId);
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100 + i);
            task.setPriority(50);
            task.setParameters("新增task测试".getBytes());
            //测试未来任务
            task.setExecuteTime(new Date().getTime() + 500 * i);
            //测试当前任务
            //task.setExecuteTime(new Date().getTime());
            long taskId = taskService.addTask(task);
            System.out.println(taskId);
        }
    }

    @Test
    public void testCancelTask() {
        taskService.cancelTask(1774445234708598786L);
    }

    @Test
    public void testPollTask() {
        //taskService.pollTask(100, 50);
        String str = "FUTURE_100_50";
        System.out.println(str.split("FUTURE_")[1]);
    }
}
