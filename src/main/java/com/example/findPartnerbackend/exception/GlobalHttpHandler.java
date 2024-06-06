package com.example.findPartnerbackend.exception;

import com.example.findPartnerbackend.common.BaseResponse;
import com.example.findPartnerbackend.common.ErrorCode;
import com.example.findPartnerbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalHttpHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandeler(BusinessException e) {
        if (e.getDescription().equals("")) {
            log.error(e.getMessage(), e);
            return ResultUtils.error(e.getCode(), e.getMessage(), "");
        } else {
            log.error(e.getMessage() + e.getDescription(), e);
            return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
        }


    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandeler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }


}
