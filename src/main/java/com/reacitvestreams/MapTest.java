package com.reacitvestreams;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 21, 2020
 */
@Slf4j
class MapTest {

    public static final Flowable<Integer> COOL_SOURCE = Flowable.<Integer, Integer> generate(() -> 0, (i, emitter) -> {
        emitter.onNext(i);
        return ++i;
    }).delay(1000, TimeUnit.MILLISECONDS).doOnRequest(it -> log.error("Request: {}", it))
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .doOnNext(it -> log.error("source : {}", it));

    @SneakyThrows
    public static void main(String[] args) {
        flatMap();
        Thread.sleep(100000000);
    }

    // It will generate the size of buffer. So it make a cool data work under pull mode
    public static void g() {
        Flowable.generate(() -> 0, (s, emitter) -> {
            log.info("{}", s);
            emitter.onNext(s);
            return (++s);
        }).observeOn(Schedulers.newThread()).doOnRequest(it -> log.error("Request: {}", it))
                .doOnSubscribe(it -> it.request(5)).subscribeOn(Schedulers.newThread()).subscribe(it -> {
                    log.info("on observe: {}", it);
                    Thread.sleep(77L);
                });
    }

    // It will emitter following the speed of publisher
    public static void c() {
        Flowable.create((emitter) -> {
            for (int i = 0;; i++) {
                log.info("{}", i);
                emitter.onNext(i);
            }
        }, BackpressureStrategy.MISSING).observeOn(Schedulers.newThread())
                .doOnRequest(it -> log.error("Request: {}", it)).doOnSubscribe(it -> it.request(5))
                .subscribeOn(Schedulers.newThread()).subscribe(it -> {
                    log.info("on observe: {}", it);
                    Thread.sleep(77L);
                });
    }

    public static void normalMap() {
        COOL_SOURCE.map(it -> "map " + it).test();
    }

    public static void flatMap() {
        COOL_SOURCE.flatMap(it -> Flowable.intervalRange(0, it, 0, 500, TimeUnit.MILLISECONDS)
                .map(n -> "flatmap " + it + ": " + n).doOnNext(log::info)).test();
    }

    public static void concatMap() {
        COOL_SOURCE.concatMap(
                it -> Flowable.intervalRange(0, it, 0, 500, TimeUnit.MILLISECONDS).map(n -> "flatmap " + it + ": " + n))
                .test();
    }

    public static void switchMap() {
        COOL_SOURCE.switchMap(
                it -> Flowable.intervalRange(0, it, 0, 500, TimeUnit.MILLISECONDS).map(n -> "flatmap " + it + ": " + n))
                .test();
    }

}
