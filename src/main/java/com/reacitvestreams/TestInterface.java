package com.reacitvestreams;

import java.util.Random;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 17, 2020
 */
@Slf4j
class TestInterface {

    @SneakyThrows
    public static void main(String[] args) {
        PublishProcessor<Integer> publishProcessor = PublishProcessor.create();
        IEventManager.DEFAULT.registerPublisher(IEventManager.REPORT, publishProcessor);
        Random random = new Random(System.currentTimeMillis());
        Flowable.range(0, 100).observeOn(Schedulers.newThread()).subscribe(source -> {
            Thread.sleep(1L * random.nextInt(10));
            log.warn("emit {}", source);
            publishProcessor.onNext(source);
        });
        Thread.sleep(5000L);
        Publisher<Integer> processor = IEventManager.DEFAULT.fetchPublisher(IEventManager.REPORT);

        Flowable.fromPublisher(processor).onBackpressureBuffer(500).observeOn(Schedulers.computation()).doOnNext(it -> {
            log.info("1: {}", it);
        }).subscribe();
        Flowable.fromPublisher(processor).observeOn(Schedulers.computation()).doOnNext(it -> {
            log.info("2: {}", it);
        }).subscribe();

        Thread.sleep(1000000);
    }

}
