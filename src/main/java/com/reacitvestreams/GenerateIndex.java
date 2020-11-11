package com.reacitvestreams;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import reactor.util.function.Tuples;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 15, 2020
 */
@Slf4j
class GenerateIndex {

    public static void main(String[] args) {
        Observable<Integer> ob = Observable.generate(Holder::new, (holder, emitter) -> {
            log.info("Generate index: {}", holder.i);
            emitter.onNext(holder.i);
            return holder.increament();
        });
        Observable.intervalRange(4, 10, 0, 10, TimeUnit.MILLISECONDS).doOnNext(it -> log.info("Generate source: {}", it))
                .zipWith(ob, Tuples::of).subscribe(it -> log.info("{}", it));
    }

    static class Holder {
        int i = 0;

        Holder increament() {
            i++;
            return this;
        }
    }

}
