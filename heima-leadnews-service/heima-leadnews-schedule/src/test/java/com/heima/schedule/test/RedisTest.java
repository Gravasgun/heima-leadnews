package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private CacheService cacheService;

    @Test
    public void testList(){
        //在list左边添加元素
        //cacheService.lLeftPush("list_001","测试list的leftPush");
        //在list右边删除元素
        String s = cacheService.lRightPop("list_001");
        System.out.println(s);
    }

    @Test
    public void testZset(){
        cacheService.zAdd("zset_001","1000",1000);
        cacheService.zAdd("zset_001","4444",4444);
        cacheService.zAdd("zset_001","2222",2222);
        cacheService.zAdd("zset_001","9999",9999);
        cacheService.zAdd("zset_001","1000",10000);
    }
}
