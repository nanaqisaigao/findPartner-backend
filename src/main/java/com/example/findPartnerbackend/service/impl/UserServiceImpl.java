package com.example.findPartnerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.findPartnerbackend.common.ErrorCode;
import com.example.findPartnerbackend.constant.UserConstant;
import com.example.findPartnerbackend.exception.BusinessException;
import com.example.findPartnerbackend.mapper.UserMapper;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.model.vo.UserVo;
import com.example.findPartnerbackend.service.UserService;
import com.example.findPartnerbackend.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.findPartnerbackend.constant.UserConstant.ADMIN_ROLE;
import static com.example.findPartnerbackend.constant.UserConstant.USER_LOGIN_STATE;
import static com.sun.activation.registries.LogSupport.log;

/**
 * @author 28044
 * @description 用户服务实现类
 * @createDate 2024-05-24 15:23:03
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    /**
     * 盐值，混淆密码
     */
    private final static String SALT = "piao";
    /**
     * 用户登录状态
     */

    @Resource
    private UserMapper userMapper;

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword, String comment) throws NoSuchAlgorithmException {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NUll_ERROR, "必填字段有空值");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户小于四位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于8位");
        }
        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能包含特殊字符");
        }
        //密码重复性校验
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        //根据userAccount获取对象 这里查询了数据库   来验证账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已存在");
        }
        //2.密码加密
        String passwordMD5 = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(passwordMD5);
        user.setComment(comment);
        boolean saveResult = this.save(user);//用userMapper.insert也行

        return !saveResult ? -1 : user.getId();


    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            log("用户名或密码为空");
            throw new BusinessException(ErrorCode.NUll_ERROR, "用户名或密码为空");
        }
        if (userAccount.length() < 4) {
            log("账户长度小于4");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于4");
        }
        if (userPassword.length() < 8) {
            log("密码小于八位");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于八位");
        }
        //账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            log("账户有特殊字符");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户有特殊字符");
        }

        //2.查询用户是否存在
        String passwordMD5 = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", passwordMD5);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log("user login fail");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不存在");
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        //5.返回脱敏后用户信息
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(0);
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setComment(user.getComment());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    /**
     * 请求用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        //用collection.isEmpty(tagList)也行
        Optional<List<String>> optional = Optional.ofNullable(tagNameList);
        if (!optional.isPresent())
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入标签为空");

        /*
         */
/**
 * 方法一：
 * 从数据库查询
 *//*

        //like '%java%' and '%python%'
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        //去掉第一次数据库连接额度时间
        userMapper.selectCount(queryWrapper1);
        long startTime1 = System.currentTimeMillis();
        for (String tagName : tagNameList) {
            queryWrapper1 = queryWrapper1.like("tags", tagName);
        }
        List<User> userList1 = userMapper.selectList(queryWrapper1);
//         return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
        log.info("从数据库查询sql query time = "+(System.currentTimeMillis()-startTime1));
*/

        /**
         * 方法二：
         * 从内存查询
         */
        long startTime = System.currentTimeMillis();
        //1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        List<User> resultUserList = userList.stream().filter(user -> Optional.ofNullable(user.getTags()).isPresent()).filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (tempTagNameSet.contains(tagName))
                    return true;
            }
            return false;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        log.info("主要从内存查询sql query time = " + (System.currentTimeMillis() - startTime));
        return resultUserList;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不正确");
        }
        //管理员可以更新所有用户，不是管理员，只允许更新自己的
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限更新");
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR, "要修改的用户不存在");
        }
        return userMapper.updateById(user);

    }

    /**
     * 获取当前登录的用户信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (!Optional.ofNullable(request).isPresent())
            return null;
        Object currentLoginUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (!Optional.ofNullable(currentLoginUser).isPresent())
            throw new BusinessException(ErrorCode.NUll_ERROR, "当前未登录");
        return (User) currentLoginUser;
    }

    /**
     * 匹配算法
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度，用来存放各个用户的与当前用户的标签相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        //找到符合条件用户，并与之id对应
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        //根据指定顺序的id，从map中找，按序插入最终的用户表
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    /**
     * 匹配标签差距最大的用户
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsersReverse(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度，用来存放各个用户的与当前用户的标签相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由大到小排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (b.getValue() - a.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表  这里先把需要的userid找出来，再进行查表，节省时间
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        //找到符合条件用户，并与之id对应
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        //根据指定顺序的id，从map中找，按序插入最终的用户表
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    /**
     * 判断是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User user) {
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }


}


