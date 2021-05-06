package com.spring.feature;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbodong
 * @date 2021/05/06 10:37
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
public class LiteConfig {

    @Bean
    private TryA b1() {
        log.info("b1");
        return new TryA(null);
    }

    @Bean
    private TryA b2(@Qualifier("b1") TryA b1) {
        log.info("b2");
        return new TryA(b1);
    }

    @Bean
    private TryA b3(@Qualifier("b1") TryA b1) {
        log.info("b3");
        return new TryA(b1);
    }

}
