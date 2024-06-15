package com.example.findPartnerbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.findPartnerbackend.model.domain.Team;
import com.example.findPartnerbackend.service.TeamService;
import com.example.findPartnerbackend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 28044
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-15 11:09:11
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




