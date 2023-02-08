package com.itheima.sh.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    //自动装配redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试redis基础环境
     */
    @Test
    public void testRedis() {
       //1.使用模板对象调用方法获取操作值是String的接口对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //2.使用对象调用方法向redis中存储数据
        valueOperations.set("username","锁哥");
        //3.获取数据
        System.out.println(valueOperations.get("username"));
    }
}
