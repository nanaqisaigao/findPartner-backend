package com.example.findPartnerbackend.dto;

import com.example.findPartnerbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     *
     */
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
    private Integer maxNum;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍状态：0-公开，1-私有，2-加密
     */
    private Integer status;



}
