package com.example.findPartnerbackend.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户注册请体
 */
@Getter
@Setter
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -2930593011248162395L;
    private String userAccount, userPassword, checkPassword,comment;


}
