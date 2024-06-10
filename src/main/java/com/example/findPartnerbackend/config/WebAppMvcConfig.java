package com.example.findPartnerbackend.config;

import cn.hutool.core.date.DatePattern;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
import java.time.format.DateTimeFormatter;

/**
 * 解决Knife4j生成接口文档404的问题
 */
@Configuration
public class WebAppMvcConfig implements WebMvcConfigurer {
 
   /* @Override
    public void addInterceptors(InterceptorRegistry registry) {
        *//**
         * 注册自定义的显示 ResponseResult 注解的拦截器
         *//*
        registry.addInterceptor(new ResponseResultInterceptor())
                // 拦截配置
                .addPathPatterns("/**")
                // 排除配置
                .excludePathPatterns("/error", "/login**");
    }
 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
 
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
 
        /** 配置knife4j 显示文档 */
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
 
        /**
         * 配置swagger-ui显示文档
         */
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        /** 公共部分内容 */
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
 
}