package com.example.findPartnerbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称 
     */
    private String tagName;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 父标签 id
     */
    private Long parentId;

    /**
     * 0-不是 1-是
     */
    private Integer idParent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-否 1-是
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0：普通用户 1：管理员
     */
    private Integer userRole;

    /**
     * 备注
     */
    private String comment;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}