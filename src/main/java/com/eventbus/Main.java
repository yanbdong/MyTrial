package com.eventbus;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
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
        ConfigurableApplicationContext context = builder.run();
        MessagePublisher messagePublisher = context.getBean(MessagePublisher.class);
        messagePublisher.sendMessage();
        EventBusSupport eventBusSupport = context.getBean(EventBusSupport.class);
        eventBusSupport.post(MessageEvent.builder().id(7).name("sync").build());
        // Cannot stop executor?
//        eventBusSupport.postAsync(MessageEvent.builder().id(8).name("aSync").build());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("yandbong: {}, {}, {}", event.getClass(), event.getSource(), event.toString());
    }
}
