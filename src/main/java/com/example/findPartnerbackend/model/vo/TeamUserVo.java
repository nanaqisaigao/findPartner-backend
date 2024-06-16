package com.example.findPartnerbackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类(对返回结果的封装)，（脱敏）
 */
@Data
public class TeamUserVo implements Serializable {

    private static final long serialVersionUID = -4628710920544017483L;
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
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
    /**
     * 入队用户列表
     */
    private UserVo createUser;

}
