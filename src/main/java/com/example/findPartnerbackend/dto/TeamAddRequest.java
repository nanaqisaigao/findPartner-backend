package com.example.findPartnerbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class TeamAddRequest {
    private Long id;

    /**
     *  队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxnum;

    /**
     * 过期时间
     */
    private Date expiretime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍状态：0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 用户密码
     */
    private String password;

}
