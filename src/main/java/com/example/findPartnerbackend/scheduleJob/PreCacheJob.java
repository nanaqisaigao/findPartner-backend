package com.example.findPartnerbackend.scheduleJob;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.findPartnerbackend.mapper.UserMapper;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserList = Arrays.asList(1L, 2L, 3L);

    // 每天凌晨执行,加载预热缓存用户
    @Scheduled(cron = "0 0 0 * * ?")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("findPartner:precachejob:docache:lock");
        try {       //锁的等待时间      //锁的过期，设置为-1（开启开门狗）
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //判断是不是当前线程加的锁
            if(lock.isHeldByCurrentThread()){
                //释放锁
                lock.unlock();
            }
        }
    }
}
