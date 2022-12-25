package com.ant.little.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ant.little"})
@MapperScan(basePackages = {"com.ant.little.core.dao"})
public class WxCloudRunApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxCloudRunApplication.class, args);
    }
}
