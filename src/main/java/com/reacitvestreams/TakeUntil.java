package com.reacitvestreams;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vavr.API;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 18, 2020
 */
@Slf4j
class TakeUntil {

    @SneakyThrows
    public static void main(String[] args) {
        Flowable.intervalRange(9, 19, 0, 1, TimeUnit.SECONDS)
                .takeUntil(Flowable.timer(5, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                        .doOnSubscribe(it -> log.info("subscribe")).doOnNext(it -> log.info("next"))
                        .doOnComplete(() -> log.info("complete")))
                .doOnNext(API::println).doOnRequest(it -> API.println("request: " + it))
                .subscribe(it -> log.info("in: " + it));
        Thread.sleep(10000L);
    }
}
