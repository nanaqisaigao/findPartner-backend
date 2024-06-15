package com.example.findPartnerbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数,用来被继承
 */
@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = -7774831586224518948L;
    /**
     * 页面大小
     */
    protected  int pageSize=20;
    /**
     * 当前是第几页
     */
    protected  int pageNumber=1;
}
