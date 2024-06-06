package com.example.findPartnerbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.findPartnerbackend.mapper")
public class findPartnerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(findPartnerBackendApplication.class, args);
    }

}
