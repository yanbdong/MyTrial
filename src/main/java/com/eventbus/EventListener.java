package com.eventbus;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 07, 2020
 */
@Component
@Slf4j
public class EventListener {

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        log.info("Subscribe message:{}", event);
    }

}
