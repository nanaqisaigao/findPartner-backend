package com.example.useCenterManageback;

import com.example.useCenterManageback.mapper.UserMapper;
import com.example.useCenterManageback.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class SampleTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(userList.size() , 5);
        userList.forEach(System.out::println);
        System.out.println(userMapper.selectById(1));
    }

}