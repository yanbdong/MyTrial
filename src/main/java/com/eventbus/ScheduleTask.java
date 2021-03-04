package com.eventbus;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 20, 2021
 */
@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class ScheduleTask {

    @Scheduled(fixedDelay = 1000)
    public void t() throws InterruptedException {
        Thread.sleep(500);
    }
}
