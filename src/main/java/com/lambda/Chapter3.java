package com.lambda;


import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter3 {

    public static <T, U> U foldLeft(Seq<T> list, U identify, Function1<U, Function1<T, U>> f) {
        U r = identify;
        for (T t : list) {
            r = f.apply(r).apply(t);
        }
        return r;
    }

    public static <T, U> U foldRight(Seq<T> list, U identify, Function1<T, Function1<U, U>> f) {
        U r = identify;
        for (int i = list.size() - 1; i >= 0; i--) {
            r = f.apply(list.get(i)).apply(r);
        }
        return r;
    }

    public static <T> Seq<T> reverse(Seq<T> list) {
        Seq<T> seq = List.empty();
        for (int i = list.size() - 1; i >= 0; i--) {
            seq.append(list.get(i));
        }
        return seq;

    }

    public static <T> Seq<T> prepend(T t, Seq<T> list) {
        return foldLeft(list, List.of(t), it -> it::append);
    }

    public static <T> Seq<T> reserve(Seq<T> list) {
        return foldLeft(list, (Seq<T>) List.<T>empty(), x -> y -> prepend(y, x));
    }

}