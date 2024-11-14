package com.chen.chenojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.chen.chenojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.chen")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.chen.chenojbackendserviceclient.service"})
public class ChenojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChenojBackendQuestionServiceApplication.class, args);
    }

}
