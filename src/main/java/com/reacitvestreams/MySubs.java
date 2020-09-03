package com.reacitvestreams;

import java.util.List;
import java.util.function.Consumer;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.core.FlowableSubscriber;
import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 19, 2020
 */
@Slf4j
@Builder
class MySubs<T> implements FlowableSubscriber<T> {

    @Builder.Default
    private String tag = "SB";
    private Consumer<Subscription> requestHandler;
    @Singular
    private List<String> sbs;

    @Override
    public void onSubscribe(Subscription s) {
        log.info("onSubscribe {}", tag);
        if (null != requestHandler) {
            requestHandler.accept(s);
        }
    }

    @Override
    public void onNext(T integer) {
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

    public static void print(Object s) {
        log.info(s.toString());
    }
}
