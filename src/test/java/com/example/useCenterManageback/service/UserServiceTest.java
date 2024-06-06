package com.example.useCenterManageback.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.useCenterManageback.mapper.UserMapper;
import com.example.useCenterManageback.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("ljp");
        user.setUserAccount("123");
        user.setAvatarUrl("https://picsum.photos/id/237/200/300");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);

    }

    /**
     * 测试用户注册接口是否正常
     * @throws NoSuchAlgorithmException
     */
    @Test
    void userRegister() throws NoSuchAlgorithmException {
        String userAccount = "piao";
        int flag = 1;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
             flag = 0;
        }

        String userPassword = "12345678";
        String checkPassword = "12345678";
        String comment = "hello";
        long resultid;
        if (flag == 1) {
            //正常情况
            resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
            Assertions.assertTrue(resultid > 0);
        }
        /**
         * 账户
         */
        //账户长度不小于四位断言
        userAccount = "pu";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);
        //账户不含特殊字符
        userAccount = "p**!u";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);
        //账户重复
        userAccount = "piao";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);
        /**
         * 密码
         */
        //密码小于8位数
        userAccount = "passLessThanEight";
        userPassword = "123";
        checkPassword = "123";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);
        //密码为空断言
        userAccount = "passNull";
        userPassword = "";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);
        //两次密码不一样
        userAccount = "pasSame";
        userPassword = "12345678";
        checkPassword = "111111111";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertEquals(-1, resultid);


        //新增用户测试   每次用户名都要改
        userAccount = "commentOk";
        userPassword = "12345678";
        checkPassword = "12345678";
        resultid = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        Assertions.assertTrue(resultid > 0);
    }
}