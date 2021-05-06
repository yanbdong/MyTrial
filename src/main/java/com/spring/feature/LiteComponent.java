package com.spring.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbodong
 * @date 2021/05/06 10:38
 **/
@Component
@Slf4j
public class LiteComponent {

    @Qualifier("tryA")
    @Autowired
    Object tryA;

    @Bean
    private TryA tryA(@Qualifier("b3") TryA another) {
        return new TryA().setS("LiteComponent").setAnother(another);
    }

//    @Bean
//    private Object c2(Object c1) {
//        log.info("c2");
//        return new Object();
//    }
//
//    @Bean
//    private Object c3(Object b1) {
//        log.info("c3");
//        return new Object();
//    }
}
