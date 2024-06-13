package com.example.findPartnerbackend.once;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class insertUsers {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;
    //计时
    StopWatch stopWatch = new StopWatch();

    /**
     * 批量插入用户
     */
    @Transactional
//    @Scheduled(initialDelay = 3000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        final int INSERT_NUM = 500;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
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

            stopWatch.start();
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
           /* userMapper.insert(user);
            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeMillis());*/
        }
        userService.saveBatch(userList);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
