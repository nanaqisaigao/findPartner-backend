/*
package com.example.findPartnerbackend.once;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import com.example.findPartnerbackend.mapper.UserMapper;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

//component把这个类编程一个bean，启动的时候被spring加载
@Component
public class insertUsersConcurrence {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;
    //计时
    StopWatch stopWatch = new StopWatch();
    //自己建的线程池
    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
    */
/**
     * 批量插入用户
     *//*

    @Transactional
//    @Scheduled(initialDelay = 3000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        //共插多少条
        final int INSERT_NUM = 10000;
        //分10个线程
        int group = 10;
        //每个线程查多少
        final int batchSize = INSERT_NUM/group;
        stopWatch.start();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        int j=0;
        for (int i = 0; i < group; i++) {
            List<User> userList = new ArrayList<>();
            while(true) {
                j++;
                String randomUserAccount = RandomStringUtils.random(3, "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                String randomUserUseAvatar = RandomStringUtils.random(3, "123");
                String randomUserGender = RandomStringUtils.random(1, "01");
                String passwordMD5 = DigestUtils.md5DigestAsHex(("piao" + "12345678").getBytes(StandardCharsets.UTF_8));
                String randomUserUsePhone = RandomStringUtils.random(10, "234567890");
                String randomUserUseEmail = RandomStringUtils.random(8, "123456789");
                String randomUserUseComment = RandomStringUtils.random(8, "真善美贪嗔痴你好世界");

                int randomToSetTags = Integer.parseInt(RandomStringUtils.random(1, "123456789"));
                String tags = "[]";
                String genderToUseTags;
                if (randomUserGender.equals("0")) {
                    genderToUseTags = "男";
                } else {
                    genderToUseTags = "女";
                }
                switch (randomToSetTags) {
                    case 1:
                        tags = "[\"c++\",\"大一\",\"" + genderToUseTags + "\"]";
                        break;
                    case 2:
                        tags = "[\"java\",\"大三\",\"" + genderToUseTags + "\"]";
                        break;
                    case 3:
                        tags = "[\"python\",\"大二\",\"" + genderToUseTags + "\"]";
                        break;
                    case 4:
                        tags = "[\"puthon\",\"大一\",\"" + genderToUseTags + "\"]";
                        break;
                    case 5:
                        tags = "[\"c++\",\"大三\",\"" + genderToUseTags + "\"]";
                        break;
                    case 6:
                        tags = "[\"java\",\"大二\",\"" + genderToUseTags + "\"]";
                        break;
                    case 7:
                        tags = "[\"java\",\"大一\",\"" + genderToUseTags + "\"]";
                        break;
                    case 8:
                        tags = "[\"python\",\"大三\",\"" + genderToUseTags + "\"]";
                        break;
                    case 9:
                        tags = "[\"c++\",\"大二\",\"" + genderToUseTags + "\"]";
                        break;

                }
                User user = new User();
                user.setUsername("小" + randomUserAccount);
                user.setUserAccount(randomUserAccount);
                user.setAvatarUrl("https://picsum.photos/id/" + randomUserUseAvatar + "/200/300");
                user.setGender(Integer.parseInt(randomUserGender));
                user.setUserPassword(passwordMD5);
                user.setPhone("1" + randomUserUsePhone);
                user.setEmail(randomUserUseEmail + "@" + RandomStringUtils.random(3, "abcdefghijklmnopqrst") + ".com");
                user.setUserStatus(0);
                user.setCreateTime(new Date());
                user.setUserRole(0);
                user.setComment(randomUserUseComment);
                user.setTags(tags);
                userList.add(user);
                if (j % batchSize == 0)
                    break;
            }
            //使用默认线程池
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
               userService.saveBatch(userList,batchSize);
            });
            //使用自定义线程池
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList,batchSize);
            },executorService);
            futureList.add(future2);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
*/
