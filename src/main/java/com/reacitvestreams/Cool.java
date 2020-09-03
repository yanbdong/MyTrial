package com.reacitvestreams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
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
class Cool {

    @SneakyThrows
    public static void main(String[] args) {
        cool();
    }

    @SneakyThrows
    private static void con() {
        Random random = new Random(System.currentTimeMillis());
        ConnectableObservable<Integer> publisher = Observable.<Integer> create(emitter -> {
            CheckedFunction1<Integer, Void> f = i -> {
                Thread.sleep(100L);
                emitter.onNext(i);
                log.info("emit {}", i);
                return null;
            };
            log.info("emitter");
            List.range(0, 20).map(it -> Try.of(() -> it)).map(it -> it.mapTry(f));
            emitter.onComplete();
        }).publish();
        Thread.sleep(1000L);
        publisher.subscribeOn(Schedulers.computation()).observeOn(Schedulers.newThread())
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable s) {
                        log.info("publisher 1 onSubscribe");
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
        Thread.sleep(2000L);
        publisher.observeOn(Schedulers.single()).subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable s) {
                log.info("publisher 2 onSubscribe");
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
        publisher.connect();
        Thread.sleep(1000);
    }

    @SneakyThrows
    private static void cool() {
        Flowable<String> publisher = Flowable.intervalRange(0, 200, 0, 1, TimeUnit.SECONDS)
                .map(it -> DateTimeFormatter.ISO_TIME.format(LocalDateTime.now()));
        publisher.subscribeOn(Schedulers.newThread()).subscribe(
                new MySubs.MySubsBuilder<>().tag("1").requestHandler(it -> it.request(Long.MAX_VALUE)).build());
        Thread.sleep(2550);
        publisher.subscribeOn(Schedulers.newThread()).subscribe(
                new MySubs.MySubsBuilder<>().tag("2").requestHandler(it -> it.request(Long.MAX_VALUE)).build());
        Thread.sleep(100000);
    }

}
