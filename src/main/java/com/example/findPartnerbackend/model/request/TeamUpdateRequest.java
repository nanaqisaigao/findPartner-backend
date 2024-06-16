package com.example.findPartnerbackend.model.request;

import com.example.findPartnerbackend.model.vo.UserVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -4828889703673794480L;

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
     * 过期时间
     */
    private Date expiretime;

    /**
     * 最大人数
     */
    private Integer maxnum;

    /**
     * 队伍状态：0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 团队密码
     */
    private String password;

}
