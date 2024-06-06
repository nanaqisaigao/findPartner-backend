package com.example.useCenterManageback.service;

import com.alibaba.fastjson2.JSONObject;
import com.example.useCenterManageback.common.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 联系redis的使用
 */
@SpringBootTest
public class RedisTest {

    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() {
        //创建对象
        BaseResponse<String> response = new BaseResponse<>(100,"try","try","try");
        //手动序列化
        String jsonString = JSONObject.toJSONString(response);
        //写入数据
        stringRedisTemplate.opsForValue().set("Response:1",jsonString);
        //获取数据
        String result = stringRedisTemplate.opsForValue().get("Response:1");

        JSONObject jsonObject = JSONObject.parseObject(result);

        System.out.println(result);
        System.out.println(jsonObject);
        System.out.println(jsonObject.toString());
    }



}
