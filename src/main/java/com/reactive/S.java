package com.reactive;

import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 08, 2020
 */
@Slf4j
class S {

    public static void main(String[] args) {
        Observable.range(1, 3).doOnNext(it -> log.info("Observable doOnNext " + it))
                .doOnSubscribe(it -> log.info("Observable doOnSubscribe"))
                .doOnTerminate(() -> log.info("Observable doOnTerminate"))
                .doOnComplete(() -> log.info("Observable doOnComplete"))
                .doAfterTerminate(() -> log.info("Observable doAfterTerminate"))
                .doOnDispose(() -> log.info("Observable doOnDispose")).collect(Collectors.toList())
                .doAfterTerminate(() -> log.info("Single doAfterTerminate 1"))
                .doAfterTerminate(() -> log.info("Single doAfterTerminate 2"))
                .doFinally(() -> log.info("Single doFinally 1")).doOnSubscribe(it -> log.info("Single doOnSubscribe"))
                .doOnSuccess(it -> log.info("Single doOnSuccess")).doOnDispose(() -> log.info("Single doOnDispose"))
                .doAfterSuccess(it -> log.info("Single doAfterSuccess"))
                .doOnEvent((it, i) -> log.info("Single doOnEvent 1"))
                .doOnEvent((it, i) -> log.info("Single doOnEvent 2"))
                .doOnTerminate(() -> log.info("Single doOnTerminate 1"))
                .doOnTerminate(() -> log.info("Single doOnTerminate 2")).doFinally(() -> log.info("Single doFinally 2"))
                .flatMapObservable(Observable::fromIterable).doOnNext(it -> log.info("After Observable doOnNext " + it))
                .doOnSubscribe(it -> log.info("After Observable doOnSubscribe"))
                .doOnTerminate(() -> log.info("After Observable doOnTerminate"))
                .doOnComplete(() -> log.info("After Observable doOnComplete"))
                .doAfterTerminate(() -> log.info("After Observable doAfterTerminate"))
                .doOnDispose(() -> log.info("After Observable doOnDispose"))
                .subscribe(it -> log.info("subscribe"));
    }

}
