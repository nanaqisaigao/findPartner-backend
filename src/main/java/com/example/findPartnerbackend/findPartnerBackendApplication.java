package com.example.findPartnerbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@SpringBootApplication
@MapperScan("com.example.findPartnerbackend.mapper")
public class findPartnerBackendApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(findPartnerBackendApplication.class, args);
    }

}
