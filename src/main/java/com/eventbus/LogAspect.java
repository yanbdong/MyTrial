package com.eventbus;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 18, 2021
 */
@Component
@Aspect
@Slf4j
class LogAspect {

    @Pointcut("this(com.eventbus.MessageEvent)")
    public void a() {

    }

    @Before("a()")
    public void xxx() {
        log.info("I ...");
    }

    @Before("execution(public * com.eventbus.MessageEvent.*(..))")
    public void why() {
        log.info("B ...");
    }

    @Before("execution(public * com.eventbus.MessagePublisher.*(..))")
    public void whyX() {
        log.info("MessagePublisher ...");
    }
}
