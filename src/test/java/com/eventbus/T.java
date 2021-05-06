package com.eventbus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zalando.stups.boot.eventbus.EventBusSupport;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 10, 2020
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class T {

    @Autowired
    private EventBusSupport mEventBusSupport;

    @Test
    void testSimple() {
        mEventBusSupport.post(new MessageEvent().id(1).name(""));
    }
}
