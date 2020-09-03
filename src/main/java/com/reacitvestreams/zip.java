package com.reacitvestreams;

import io.reactivex.rxjava3.core.Flowable;
import io.vavr.API;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 18, 2020
 */
class zip {

    public static void main(String[] args) {
        Flowable.range(9, 19).doOnNext(API::println)
                .zipWith(Flowable.just("apple", "banana", "oracle"), (i, s) -> s + i).doOnNext(API::println)
                .doOnRequest(it->API.println("request: " + it))
                .subscribe(it->API.println("in: " + it));

    }
}
