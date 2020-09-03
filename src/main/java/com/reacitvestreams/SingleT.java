package com.reacitvestreams;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vavr.API;
import lombok.SneakyThrows;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 21, 2020
 */
class
SingleT {

    @SneakyThrows
    public static void main(String[] args) {
        Observable<Long> p = Observable.intervalRange(0,5,0,1, TimeUnit.SECONDS);
        p.timeInterval().forEach(API::println);
        p.timestamp().forEach(API::println);
        Thread.sleep(100000);
    }
}
