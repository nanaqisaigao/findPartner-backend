package com.example.findPartnerbackend.service;

import com.example.findPartnerbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.findPartnerbackend.model.domain.User;
import com.example.findPartnerbackend.model.dto.TeamQuery;
import com.example.findPartnerbackend.model.request.TeamJoinRequest;
import com.example.findPartnerbackend.model.request.TeamUpdateRequest;
import com.example.findPartnerbackend.model.vo.TeamUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 28044
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-06-15 11:09:11
*/
public interface TeamService extends IService<Team> {
    /**创建队伍
     *
     * @param team
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser );

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
