package com.example.findPartnerbackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类，用来脱敏
 */
@Data
public class UserVo  implements Serializable {

    private static final long serialVersionUID = -6401831217417297028L;
    /**
     *
     */
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     *
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 默认为0
     */
    private Integer userStatus;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 0：普通用户   1：管理员
     */
    private Integer userRole;

    /**
     * 备注
     */
    private String comment;
    /**
     * 用户标签 是JSON
     */
    private String tags;
}
