package com.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.vavr.API;
import lombok.SneakyThrows;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 14, 2020
 */
class Ob {

    @SneakyThrows
    public static void main(String[] args) {
//        Observable<Integer> ob = Observable.create(emitter -> {
//            CheckedFunction1<Integer, Void> f = i -> {
//                Thread.sleep(100);
//                emitter.onNext(i);
//                return null;
//            };
//            API.println("emitter");
//            List.range(0, 10).map(it -> Try.of(() -> it)).map(it -> it.mapTry(f));
//            emitter.onComplete();
//        });
//        Thread.sleep(5000);
//
//        Disposable disposable = ob.subscribeOn(Schedulers.newThread()).subscribe(API::println, API::println,
//                () -> API.println("Complete"));
//        Thread.sleep(500);
//        disposable.dispose();
//        Thread.sleep(1000);
//        ob.subscribe(API::println, API::println, () -> API.println("Complete"));

        Observable<String> left = Observable.just("l1", "l2", "l3", "l4", "l5", "l6");
        Observable<String> right = Observable.just("r1", "r2", "r3");
        left.concatMap(it -> Observable.combineLatest(Observable.just(it), right, (l, r) -> l + ":" + r))
                .doOnNext(API::println).count().subscribe((Consumer<Long>) API::println);
    }

}
