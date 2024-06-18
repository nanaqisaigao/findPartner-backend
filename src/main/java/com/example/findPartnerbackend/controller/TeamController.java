package com.example.findPartnerbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.findPartnerbackend.common.BaseResponse;
import com.example.findPartnerbackend.common.ErrorCode;
import com.example.findPartnerbackend.common.ResultUtils;
import com.example.findPartnerbackend.model.domain.UserTeam;
import com.example.findPartnerbackend.model.dto.TeamAddRequest;
import com.example.findPartnerbackend.model.dto.TeamQuery;
import com.example.findPartnerbackend.exception.BusinessException;
import com.example.findPartnerbackend.model.domain.Team;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.model.request.DeleteRequest;
import com.example.findPartnerbackend.model.request.TeamJoinRequest;
import com.example.findPartnerbackend.model.request.TeamQuitRequest;
import com.example.findPartnerbackend.model.request.TeamUpdateRequest;
import com.example.findPartnerbackend.model.vo.TeamUserVo;
import com.example.findPartnerbackend.service.TeamService;
import com.example.findPartnerbackend.service.UserService;
import com.example.findPartnerbackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 增
     * 创建队伍
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamAddRequest);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删
     * 删除队伍
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if(deleteRequest == null || deleteRequest.getId() <= 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();

        boolean result = teamService.deleteTeam(id,userService.getLoginUser(request));
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }
    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.quitTeam(teamQuitRequest, userService.getLoginUser(request));
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 改
     * 更改队伍信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateTeam(teamUpdateRequest,userService.getLoginUser(request));
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 查
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestParam long id) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NUll_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 根据参数差队伍列表
     * 按搜索参数，查询所有符合队伍
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, userService.isAdmin(request),0);
        return ResultUtils.success(teamList);
    }
    /**
     * 查，获取我创建的队伍
     * 按搜索参数，查询所有符合队伍
     */
    @GetMapping("/list/mycreate")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true,1);
        return ResultUtils.success(teamList);
    }

    /**
     * 查，获取我加入的队伍
     * 按搜索参数，查询所有符合队伍
     */
    @GetMapping("/list/myjoin")
    public BaseResponse<List<TeamUserVo>> listMyJoinTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        //自己在的所有队伍
        QueryWrapper <UserTeam>queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> list = userTeamService.list(queryWrapper);
        List<Long> teamIdList = list.stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        //自己创建的队伍

        teamQuery.setIdList(teamIdList);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true,1);
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查所有队伍
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        try {
            // 复制属性
            BeanUtils.copyProperties(team,teamQuery);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Page<Team> page = new Page<>(teamQuery.getPageNumber(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page,queryWrapper);
        return ResultUtils.success(teamPage);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.joinTeam(teamJoinRequest, userService.getLoginUser(request));
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }




}
