package com.example.findPartnerbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.findPartnerbackend.common.BaseResponse;
import com.example.findPartnerbackend.common.ErrorCode;
import com.example.findPartnerbackend.common.ResultUtils;
import com.example.findPartnerbackend.constant.UserConstant;
import com.example.findPartnerbackend.exception.BusinessException;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.model.request.UserLoginRequest;
import com.example.findPartnerbackend.model.request.UserRegisterRequest;
import com.example.findPartnerbackend.model.vo.UserVo;
import com.example.findPartnerbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.findPartnerbackend.constant.UserConstant.ADMIN_ROLE;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) throws NoSuchAlgorithmException {
        //判断是否为空
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.NUll_ERROR);
            throw new BusinessException(ErrorCode.NUll_ERROR,"请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String comment = userRegisterRequest.getComment();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"必要参数存在空值");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword,comment);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) throws NoSuchAlgorithmException {
        //判断是否为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR,"请求为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"必要参数存在空值");
        }
        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) throws NoSuchAlgorithmException {
        //判断是否为空
        if (request == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR,"请求为空");
        }
        Integer result=userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        if(currentUser == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN,"当前无用户");

        long userId = currentUser.getId();
        //TODO 校验用户是否合法
        User user = userService.getById(userId);
        User result = userService.getSafetyUser(user);
        return ResultUtils.success(result);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        //仅管理员可以查询
        if(!userService.isAdmin(request)){
            return ResultUtils.success(new ArrayList<>());
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream()
                .map(user ->  userService.getSafetyUser(user))
                .collect(Collectors.toList());
        return ResultUtils.success(result);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request) {
        //仅管理员可以查询
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"当前用户没有权限");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不存在此用户");
        }
        Boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //1.校验参数是否为空
        if(!Optional.ofNullable(user).isPresent())
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"空对象出现");
        User loginUser = userService.getLoginUser(request);

        //2.校验权限
        //3.触发更新
        Integer result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NUll_ERROR,"标签不能为空");
        }
        List<User> users = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(users);
    }
/*  不使用的用户推荐
    @GetMapping("recommend")//pageSize每页几条，pageNum共几页
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request){
       QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> usersList = userService.list(queryWrapper);
        //分页
        Page<User> usersPageList = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
//        List<User> list = usersList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(usersPageList);
    }   */
    //用户推荐使用缓存的版本
    @GetMapping("/recommend")//pageSize每页几条，pageNum共几页
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request){
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        //设置存入的键
        String redisKey = String.format("findPartner:user:recommend:%s",loginUser.getId());
        //如果缓存中有，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if(userPage!=null){
            return ResultUtils.success(userPage);
        }
        //如果无缓存，从数据库查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> usersPageList = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        //写入缓存
        try {
            valueOperations.set(redisKey, usersPageList,100000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("redis set key error",e);
        }
        return ResultUtils.success(usersPageList);
    }

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }
    @GetMapping("/match/reverse")
    public BaseResponse<List<User>> matchUsersReverse(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsersReverse(num, user));
    }



}
