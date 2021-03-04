package com.eventbus;

import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@Component
@Slf4j
public class MessagePublisher {

    @Autowired
    private EventBus eventBus;

    public void sendMessage(MessageEvent event) {
        log.info("send message...");
        this.eventBus.post(event);
    }

}
