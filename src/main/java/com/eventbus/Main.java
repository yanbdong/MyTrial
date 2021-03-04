package com.eventbus;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.zalando.stups.boot.eventbus.EventBusSupport;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@SpringBootApplication
@Slf4j
public class Main implements ApplicationListener<ApplicationEvent> {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.web(WebApplicationType.SERVLET);
        ConfigurableApplicationContext context = builder.run();
        MessagePublisher messagePublisher = context.getBean(MessagePublisher.class);

        EventBusSupport eventBusSupport = context.getBean(EventBusSupport.class);

        MessageEvent event = context.getBean(MessageEvent.class);
        event.setId(7);
        event.setName("sync");
        eventBusSupport.post(event);

        messagePublisher.sendMessage(event);
        // Cannot stop executor?
//        eventBusSupport.postAsync(MessageEvent.builder().id(8).name("aSync").build());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("yandbong: {}, {}, {}", event.getClass(), event.getSource(), event.toString());
    }

    @Bean
    public Advisor jamonAdvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("target(com.eventbus.ScheduleTask)");
        advisor.setAdvice(new JamonPerformanceMonitorInterceptor(false, true));
        return advisor;
    }
}
