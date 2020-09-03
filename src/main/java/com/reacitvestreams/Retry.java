package com.reacitvestreams;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.vavr.API;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 18, 2020
 */

class Retry {

    public static void main(String[] args) {
        Flowable<Person> t = Flowable.<Person> create(emitter -> {
            CheckedFunction1<Integer, Void> f = i -> {
                API.println("emit " + i);
                Thread.sleep(100L);
                emitter.onNext(new Person(DateTimeFormatter.ISO_TIME.format(LocalDateTime.now())));
                return null;
            };
            List.range(1, 4).map(it -> Try.of(() -> it)).map(it -> it.mapTry(f));
            API.println("emit complete");
            emitter.onComplete();
        }, BackpressureStrategy.ERROR).repeatWhen(f -> {
            return f.doOnNext(it -> API.println("yanbdong: " + it)).zipWith(Flowable.range(0, 5), (ignore, i) -> i)
                    .doOnNext(it -> API.println("yanbdong after: " + it));
        });
        t.doOnSubscribe(it -> it.request(100)).subscribe(new FlowableSubscriber<Person>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                API.println("onSubscribe");
            }

            @Override
            public void onNext(Person person) {
                API.println("onNext: " + person);
            }

            @Override
            public void onError(Throwable t) {
                API.println("onError");

            }

            @Override
            public void onComplete() {
                API.println("onComplete");

            }
        });
    }

    @ToString
    @AllArgsConstructor
    public static class Person {

        private String name;
    }

}
