package com.reactive;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author yanbdong@cienet.com.cn
 * @since Aug 14, 2020
 */
class Sub {

    public static void main(String[] args) {
        Observable<Integer> deferred = Observable.defer(() -> null);
        T t = (T) new Object();
    }

    public interface T {
        void t() throws Throwable;
    }

}
