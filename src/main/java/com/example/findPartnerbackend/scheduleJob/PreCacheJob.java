package com.example.findPartnerbackend.scheduleJob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.findPartnerbackend.mapper.UserMapper;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;

    private List<Long> mainUserList = Arrays.asList(1L, 2L, 3L);
    // 每天凌晨执行,加载预热缓存用户
    @Scheduled(cron = "0 0 0 * * ?")
    public void doCacheRecommendUser() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        for (Long userId : mainUserList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            String redisKey = String.format("findPartner:user:recommend:%s", userId);
            Page<User> usersPageList = userService.page(new Page<>(1, 20), queryWrapper);
            //写入缓存
            try {
                valueOperations.set(redisKey, usersPageList, 100000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        }
    }
}
