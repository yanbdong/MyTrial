package com.eventbus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@Component
@Slf4j
public class MessagePublisher {

    private final EventBus eventBus;

    @Autowired
    public MessagePublisher(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendMessage() {
        this.eventBus.post(MessageEvent.builder().id(1).name("test").build());
        log.info("send message...");
    }

}
