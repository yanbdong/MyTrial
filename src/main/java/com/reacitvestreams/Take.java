package com.reacitvestreams;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 18, 2020
 */
@Slf4j
class Take {

    @SneakyThrows
    public static void main(String[] args) {
        Flowable<Integer> f1 = Flowable.just(1).delay(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread())
                .doOnNext(MySubs::print);
        Flowable<Integer> f2 = Flowable.just(2).delay(1000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread())
                .doOnNext(MySubs::print);
        f1.mergeWith(f2).doOnNext(it -> MySubs.print("before " + it)).take(1).doOnNext(it -> MySubs.print("on " + it))
                .subscribe(MySubs.builder().requestHandler(it -> it.request(100)).build());
        Thread.sleep(100000L);
    }
}
