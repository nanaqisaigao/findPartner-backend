package com.example.findPartnerbackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 3662822827923610069L;

  private Long teamId;

    /**
     * 团队密码
     */
    private String password;

}
