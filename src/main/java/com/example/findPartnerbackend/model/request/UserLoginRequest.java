package com.example.findPartnerbackend.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户注册请体
 */
@Getter
@Setter
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7904353826182099462L;
    private String userAccount,userPassword;


}
