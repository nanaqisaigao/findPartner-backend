package com.example.findPartnerbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.findPartnerbackend.model.domain.UserTeam;
import com.example.findPartnerbackend.service.UserTeamService;
import com.example.findPartnerbackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 28044
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2024-06-15 11:11:31
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




