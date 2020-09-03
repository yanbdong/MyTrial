package com.reacitvestreams;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 17, 2020
 */
@Slf4j
class Hot {

    @SneakyThrows
    public static void main(String[] args) {
        FlowableProcessor<Integer> processor = PublishProcessor.<Integer> create().toSerialized();
        Random random = new Random(System.currentTimeMillis());
        Flowable.range(0, 100).observeOn(Schedulers.newThread()).subscribe(source -> {
            Thread.sleep(10L * random.nextInt(10));
            log.warn("emit {}", source);
            processor.onNext(source);
        });
        // MySub mySub1 = new MySub();
        // mySub1.setTag("1");
        // mySub1.setRequestHandler(subscription -> {
        // subscription.request(10);
        // });
        // MySub mySub2 = new MySub();
        // mySub2.setTag("2");
        // mySub2.setRequestHandler(subscription -> {
        // subscription.request(20);
        // });
        // processor.doOnSubscribe(it -> {
        // log.error("1: doOnSubscribe: before");
        // }).subscribeOn(Schedulers.single()).doOnSubscribe(it -> {
        // log.error("1: doOnSubscribe: after");
        // }).subscribe(mySub1);

        // processor.doOnSubscribe(it -> {
        // log.error("2: doOnSubscribe: before");
        // }).observeOn(Schedulers.io()).doOnSubscribe(it -> {
        // log.error("2: doOnSubscribe: after");
        // }).subscribe(mySub2);
        Thread.sleep(1000L);
        processor.observeOn(Schedulers.computation()).doOnRequest(it -> log.info("left request count {}", it))
                .doOnSubscribe(it->log.info("doOnSubscribe"))
                .subscribeOn(Schedulers.io(), true).subscribe(it -> log.info("Do it: {}", it));
        Thread.sleep(1000000);
    }

    @Slf4j
    @NoArgsConstructor
    static class MySub implements FlowableSubscriber<Integer> {

        @Setter
        private String tag;

        @Setter
        private Consumer<Subscription> requestHandler;

        @Override
        public void onSubscribe(Subscription s) {
            log.info("onSubscribe {}", tag);
            if (null != requestHandler) {
                requestHandler.accept(s);
            }
        }

        @Override
        public void onNext(Integer integer) {
            log.info("onNext {}: {}", tag, integer);
        }

        @Override
        public void onError(Throwable t) {
            log.info("onError {}, {}", tag, t);
        }

        @Override
        public void onComplete() {
            log.info("onComplete {}", tag);
        }
    }

    private static void t() {
    }
}
