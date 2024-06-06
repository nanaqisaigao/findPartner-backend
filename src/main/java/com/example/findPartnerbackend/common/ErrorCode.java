package com.example.findPartnerbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 全局错误码
 */

@AllArgsConstructor
public enum ErrorCode {
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NUll_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    private int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }


}
