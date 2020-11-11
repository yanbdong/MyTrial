package com.reactive;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Supplier;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuples;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 11, 2020
 */
@Slf4j
class TryWith {

    public static void main(String[] args) throws Exception {
        t2();
        Thread.sleep(10000000);

    }

    // Can not stop the never one thus the combine one can not be stopped either.
    private static void t1() {
        Observable<Long> source = Observable.fromSupplier(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            log.info("source generated {}", currentTimeMillis);
            return currentTimeMillis;
        }).delay(2, TimeUnit.SECONDS).concatWith(Observable.never()).doOnNext(it -> log.info("source doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("source doOnSubscribe"))
                .doOnComplete(() -> log.info("source doOnComplete")).doOnDispose(() -> log.info("source doOnDispose"))
                .doOnTerminate(() -> log.info("source doOnTerminate"))
                .doAfterTerminate(() -> log.info("source doAfterTerminate"));
        Observable<Long> data = Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .doOnNext(it -> log.info("data doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("data doOnSubscribe")).doOnDispose(() -> log.info("data doOnDispose"))
                .doOnTerminate(() -> log.info("data doOnTerminate"))
                .doAfterTerminate(() -> log.info("data doAfterTerminate"));
        Observable.combineLatest(source, data, Tuples::of).doOnNext(it -> log.info("combine doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("combine doOnSubscribe"))
                .doOnDispose(() -> log.info("combine doOnDispose"))
                .doOnTerminate(() -> log.info("combine doOnTerminate"))
                .doAfterTerminate(() -> log.info("combine doAfterTerminate")).subscribe(it -> log.info("{}", it));
    }

    private static void t2() {
        // 在总结果上使用资源
        Observable.using(
                // 资源开启
                () -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    log.info("source generated {}", currentTimeMillis);
                    return currentTimeMillis;
                },
                // 全部数据
                ignore -> Observable.range(1, 10)
                        // 总结果收集
                        .doOnNext(it -> log.info("Master receive {}", it))
                        // 进入每一轮
                        .window(2)
                        // 为每一轮加入额外信息
                        .scanWith((Supplier<Holder<Observable<Integer>>>) Holder::new, Holder::add)
                        // 跳过空的额外信息，会把初始supplier先发出一份
                        .skip(1)
                        // 解map，不然内部的流无法订阅上，就不执行了
                        .concatMap(
                                // 每轮的结果记录
                                holder -> Observable.using(() -> {
                            log.info("sub source {} generated", holder.number);
                            return holder;
                        }, use -> holder.data
                                        // 记录
                                                .doOnNext(it -> log.info("sub {} receive {}", use.number, it)),
                                it -> log.info("Clean {}", it.number), true)
                                .doOnSubscribe(h -> log.info("sub source {} doOnSubscribe", holder.number))),
                // 资源关闭
                it -> log.info("Clean {}", it)).subscribe();
    }

    private static void t3() {
        Observable<Long> source = Observable.fromSupplier(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            log.info("source generated {}", currentTimeMillis);
            return currentTimeMillis;
        }).delay(2, TimeUnit.SECONDS).doOnNext(it -> log.info("source doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("source doOnSubscribe"))
                .doOnComplete(() -> log.info("source doOnComplete")).doOnDispose(() -> log.info("source doOnDispose"))
                .doOnTerminate(() -> log.info("source doOnTerminate"))
                .doAfterTerminate(() -> log.info("source doAfterTerminate"));
        Observable<Long> data = Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .doOnNext(it -> log.info("data doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("data doOnSubscribe")).doOnDispose(() -> log.info("data doOnDispose"))
                .doOnTerminate(() -> log.info("data doOnTerminate"))
                .doAfterTerminate(() -> log.info("data doAfterTerminate"));
        data.join(source, it -> Observable.never(), it -> Observable.never(), Tuples::of)
                .doOnNext(it -> log.info("combine doOnNext: {}", it))
                .doOnSubscribe(ignore -> log.info("combine doOnSubscribe"))
                .doOnDispose(() -> log.info("combine doOnDispose"))
                .doOnTerminate(() -> log.info("combine doOnTerminate"))
                .doAfterTerminate(() -> log.info("combine doAfterTerminate")).subscribe(it -> log.info("{}", it));
    }

    static class Holder<T> {
        int number = 0;
        T data;

        Holder<T> add(T d) {
            data = d;
            number++;
            return this;
        }
    }

}
