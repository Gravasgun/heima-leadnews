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
            task.setExecuteTime(new Date().getTime() + 5000 * i);
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

    @Test
    public void test(){
        System.out.println("Teamname:lichunyao");
        System.out.println("Member 1:lichunyao");
        System.out.println("Email 1:123456789@qq.com");
        System.out.println("Rotate:Version = native_rotate_exch_ij:exchange i and j in the base of native rotate:");
        System.out.println("Dim            256      512     1024   2048    4096   Mean");
        System.out.println("Your CPEs      2.1      3.1     5.4    28.8    34.5");
        System.out.println("Baseline       14.7     40.1    46.4   65.9    94.5");
        System.out.println("Speedup        7.0      12.8    8.6    2.3     2.7    5.5");

        System.out.println("Rotate:Version = rotate:Current working version (use loop unrolling int the base of native_rotate_exch_ij)");
        System.out.println("Dim            256      512     1024   2048    4096   Mean");
        System.out.println("Your CPEs      1.5      1.4     2.7    3.9    34.5");
        System.out.println("Baseline       14.7     40.1    46.4   65.9    94.5");
        System.out.println("Speedup        9.9      29.6    17.2   24.7     2.7   18.4");

        System.out.println("Smooth:Version = smooth:split area:");
        System.out.println("Dim            256      512     1024   2048    4096   Mean");
        System.out.println("Your CPEs      10.3     10.7    11.1   11.9    11.8");
        System.out.println("Baseline       695.0    698.0   702.0  717.0   722.0");
        System.out.println("Speedup        67.8     65.4    63.4   60.3    61.2   63.5");

        System.out.println("Smooth:Version = smooth:use loop unrolling in the base of smooth1:");
        System.out.println("Dim            256      512     1024   2048    4096   Mean");
        System.out.println("Your CPEs      10.2     10.1    10.4   11.3    11.2");
        System.out.println("Baseline       695.0    698.0   702.0  717.0   722.0");
        System.out.println("Speedup        68.5     69.2    67.4   63.4    64.5   66.5");
    }
}
