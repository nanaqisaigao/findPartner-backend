package com.example.findPartnerbackend.service;

import com.example.findPartnerbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.findPartnerbackend.model.domain.User;

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
}
