package com.szhq.iemp.reservation.config;

import com.szhq.iemp.reservation.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisUtilBeans {

    @Resource(name = "primaryRedisTemplate")
    private RedisTemplate<String, Object> primaryRedisTemplate;
    @Resource(name = "secondRedisTemplate")
    private RedisTemplate<String, Object> secondRedisTemplate;

    @Bean(name = "primaryRedisUtil")
    public RedisUtil primaryRedisUtil() {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(primaryRedisTemplate);
        return redisUtil;
    }

    @Bean(name = "secondRedisUtil")
    public RedisUtil secondRedisUtil() {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(secondRedisTemplate);
        return redisUtil;
    }
}
