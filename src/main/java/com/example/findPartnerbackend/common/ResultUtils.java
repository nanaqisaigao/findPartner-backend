package com.example.findPartnerbackend.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    /**
     * 通用返回成功信息
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 用于定义给前端返回的异常信息，被GlobalHttpHandler的@ExceptionHandler(Exception.class)捕获
     * @param
     * @return
     */
    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,message,description);
    }

    public static BaseResponse error(ErrorCode errorCode){
        return  new BaseResponse<>(errorCode);
    }
    public static BaseResponse error(ErrorCode errorCode,String description){
        return  new BaseResponse<>(errorCode.getCode(),errorCode.getMessage(),description);
    }

    public static BaseResponse error(ErrorCode errorCode,String message,String description){
        return  new BaseResponse<>(errorCode.getCode(),message,description);
    }

}
