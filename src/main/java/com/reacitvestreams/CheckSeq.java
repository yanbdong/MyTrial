package com.reacitvestreams;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 17, 2020
 */
@Slf4j
class CheckSeq {

    @SneakyThrows
    public static void main(String[] args) {
        Hot.MySub mySub1 = new Hot.MySub();
        mySub1.setTag("1");
        mySub1.setRequestHandler(subscription -> {
            subscription.request(1);
        });
        Hot.MySub mySub2 = new Hot.MySub();
        mySub2.setTag("2");
        mySub2.setRequestHandler(subscription -> {
            subscription.request(5);
        });
        Hot.MySub mySub3 = new Hot.MySub();
        mySub3.setTag("3");
        mySub3.setRequestHandler(subscription -> {
            subscription.request(15);
        });
        Hot.MySub mySub4 = new Hot.MySub();
        mySub4.setTag("4");
        mySub4.setRequestHandler(subscription -> {
            subscription.request(50);
        });
        Flowable.create(emitter -> {
            List.range(0, 100).forEach(it -> {
                log.info("emitter {}", it);
                emitter.onNext(it);
            });
        }, BackpressureStrategy.MISSING).parallel().runOn(Schedulers.computation()) // 应该首先指定观察者线程所在的worker
                .doOnNext(it -> {
                    log.info("doOnNext {}: {}", 1, it);
                    Thread.sleep(1000);
                }) // 可以继续改变察者线程所在的worker
                .doOnNext(it -> {
                    log.info("doOnNext {}: {}", 2, it);
                }); // 只有调用subscribe，才会正真有事件发出

        Flowable.range(0, 1).observeOn(Schedulers.computation())
                .doOnNext(it -> log.info("doOnNext {}: {}", 1, it)).doOnComplete(()->log.info("Complete")).doOnRequest(it->log.info("doOnRequest"))
                .doOnSubscribe(it -> log.info("doOnSubscribe {}: {}", 1, it)).subscribeOn(Schedulers.single())
                .subscribe(it -> log.info("subscribe {}: {}", 1, it));
        Thread.sleep(1000000L);
    }

}
