package com.lambda;

import java.util.function.Supplier;

import io.vavr.API;
import io.vavr.Lazy;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter9<T> {

    private final Lazy<T> mHead;
    private final Lazy<Chapter9<T>> mTail;

    Chapter9(Supplier<T> head, Supplier<Chapter9<T>> tail) {
        this.mHead = API.Lazy(head);
        this.mTail = API.Lazy(tail);
    }

    Chapter9<T> getTail() {
        return mTail.get();
    }

    T getHead() {
        return mHead.get();
    }

    public static Chapter9<Integer> from(int i) { return new Chapter9<Integer>(() -> i, () -> from(i+ 1));};

    public static void main(String... args) {
        Chapter9<Integer> s = from(0);
        Integer integer = s.getTail().getTail().getTail().getTail().getTail().getTail().getHead();
    }
}