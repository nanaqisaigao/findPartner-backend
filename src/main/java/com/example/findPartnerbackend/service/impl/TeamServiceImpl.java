package com.example.findPartnerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.findPartnerbackend.common.ErrorCode;
import com.example.findPartnerbackend.exception.BusinessException;
import com.example.findPartnerbackend.model.domain.Team;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.model.domain.UserTeam;
import com.example.findPartnerbackend.model.dto.TeamQuery;
import com.example.findPartnerbackend.model.enums.TeamStatusEnum;
import com.example.findPartnerbackend.model.request.TeamJoinRequest;
import com.example.findPartnerbackend.model.request.TeamQuitRequest;
import com.example.findPartnerbackend.model.request.TeamUpdateRequest;
import com.example.findPartnerbackend.model.vo.TeamUserVo;
import com.example.findPartnerbackend.model.vo.UserVo;
import com.example.findPartnerbackend.service.TeamService;
import com.example.findPartnerbackend.mapper.TeamMapper;
import com.example.findPartnerbackend.service.UserService;
import com.example.findPartnerbackend.service.UserTeamService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author 28044
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-06-15 11:09:11
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1. 请求参数是否为空？
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3. 校验信息
        //  a. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxnum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        //  b. 队伍标题 <= 20
        String teamName = team.getName();
        if (StringUtils.isAnyBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
        }
        //  c. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isAnyBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不符合要求");
        }
        //  d. status 是否公开（int）不传默认为 0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不正确");
        }
        //  e. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        //5.如果status是加密状态，一定要密码 且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        //  f. 超时时间 > 当前时间
        Date expireTime = team.getExpiretime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超出时间 > 当前时间");
        }
        //  g. 校验用户最多创建 5 个队伍
        //todo 有bug。用户疯狂点击，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }

        //4. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean result = this.save(team);
        if (!result || team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxnum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            //根据状态查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());

        }
        //不展示已过期队伍
        //expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        //查询关联查询创建人信息
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVosList = new ArrayList<>();
        //遍历队伍
        for (Team team : teamList) {
            //取创建人id
            Long userId = team.getUserId();
            if (userId == null || userId <= 0) {
                continue;
            }
            //根据创建人信息找人
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            UserVo userVo = new UserVo();
            try {
                BeanUtils.copyProperties(teamUserVo, team);
                BeanUtils.copyProperties(userVo, user);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            teamUserVo.setCreateUser(userVo);
            teamUserVosList.add(teamUserVo);
        }
        //TODO 返回所有入队用户，用关联来实现
        //关联查询用户信息
        //1.自己写sql
        //查询队伍和创建人的信息
        //select * from team t left join user u on t.userId =u.id
        //查询队伍和已加入队伍成员的信息
        //select * from team t join user_team ut on t.id = ut.teamId
        //join user u on ut.userId = u.id

        return teamUserVosList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求队伍id不存在");
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍不能无密码");
            }
        }
        //只有管理员和队伍创建者才能修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Team updateTeam = new Team();
        try {
            //请求改成Team类型来update
            BeanUtils.copyProperties(updateTeam, teamUpdateRequest);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return this.updateById(updateTeam);


    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //队伍加入相关
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不合法");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        if (team.getExpiretime() != null && team.getExpiretime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }

        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "私密队伍不允许加入");
        }
        String passwordRequest = teamJoinRequest.getPassword();
        String passwordNeed = team.getPassword();
        if (teamStatusEnum == TeamStatusEnum.SECRET) {
            if (StringUtils.isBlank(passwordRequest) || !passwordNeed.equals(passwordRequest)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入加密队伍密码错误");
            }
        }
        //该用户加入队伍的数量
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        Long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "一个人最多加入和创建5个队伍");
        }
        //不能重复加入已加入队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        Long hasJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入过这个队伍");
        }

        //队伍已有的人数
        long teamHasJoinNum = this.getTeamUserCountByTeamId(teamId);
        if (teamHasJoinNum >= team.getMaxnum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }

        //加入队伍
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR, "队伍不存在");
        }
        Long userId = loginUser.getId();
        //未加入不能退出
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setUserId(userId);
        queryUserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你不是队伍成员");
        }
        //只剩自己,解散队伍
        if (this.getTeamUserCountByTeamId(teamId) == 1) {
            //删除队伍
            this.removeById(teamId);
        } else {
            //还剩至少两人,是否为队长，是队长更新team表的队长
            if (team.getUserId().equals(userId)) {
                //把队伍转移给最早加入的用户
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
//                userTeamQueryWrapper.orderByAsc("joinTime");
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamsList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamsList) || userTeamsList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");
                }
                UserTeam nextUserTeam = userTeamsList.get(1);
                //更新队伍的队长
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
            //不管是不是队长，都删除自己TeamUser表中的关系
            userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", teamId);
            userTeamQueryWrapper.eq("userId", userId);
            return userTeamService.remove(userTeamQueryWrapper);
        }
        //删除所有队伍的关系
        return userTeamService.remove(userTeamQueryWrapper);
}

    @Override
    public boolean deleteTeam(long id, User loginUser) {
        return false;
    }


    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private Long getTeamUserCountByTeamId(long teamId){
        //队伍已有的人数
        QueryWrapper userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}





