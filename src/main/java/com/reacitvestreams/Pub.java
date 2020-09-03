package com.reacitvestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 17, 2020
 */
@Slf4j
class Pub {

    @SneakyThrows
    public static void main(String[] args) {
        Flowable<Integer> publisher = Flowable.create(emitter -> {
            CheckedFunction1<Integer, Void> f = i -> {
                Thread.sleep(1);
                emitter.onNext(i);
                log.info("emit {}", i);
                return null;
            };
            log.info("emitter");
            List.range(0, 100).map(it -> Try.of(() -> it)).map(it -> it.mapTry(f));
            emitter.onComplete();
        }, BackpressureStrategy.ERROR);
        publisher.subscribeOn(Schedulers.computation(), false).observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        log.info("publisher 1 onSubscribe");
                        s.request(1);
                        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> s.request(1), 0, 1,
                                TimeUnit.SECONDS);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info("publisher 1 onNext {}", integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.info("publisher 1 onError {}", t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log.info("publisher 1 onComplete");
                    }
                });
        publisher.subscribeOn(Schedulers.newThread()).subscribe(new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                log.info("publisher 2 onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("publisher 2 onNext {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("publisher 2 onError {}", t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("publisher 2 onComplete");
            }
        });
        Thread.sleep(1000000L);
    }

}
