package com.database;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 02, 2020
 */
@Component
@MapperScan("com.database.mapper")
@Slf4j
public class MyBatisStarter {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplication(MyBatisStarter.class).run();
        MyBatisStarter starter = context.getBean(MyBatisStarter.class);
    }
}
