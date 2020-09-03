package com.lambda;


import io.vavr.API;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Function2;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter4 {

    /**
     * Tail calling
     *
     * @param <T>
     */
    public interface TailCall<T> {

        /**
         * Get value
         *
         * @return
         */
        T eval();

        static <T> TailCall<T> ret(T t) {
            return new Return<>(t);
        }

        static <T> TailCall<T> sus(Function0<TailCall<T>> s) {
            return new Suspend<>(s);
        }
    }


    private static class Return<T> implements TailCall<T> {

        private final T mT;

        private Return(T t) {
            mT = t;
        }


        @Override
        public T eval() {
            return mT;
        }
    }

    private static class Suspend<T> implements TailCall<T> {

        private final Function0<TailCall<T>> mNext;

        private Suspend(Function0<TailCall<T>> next) {
            mNext = next;
        }

        @Override
        public T eval() {
            TailCall<T> end = this;
            while (end instanceof Suspend) {
                end = ((Suspend<T>) end).mNext.apply();
            }
            return end.eval();
        }
    }

    static int add(int x, int y) {
        return y == 0 ? x : add(++x, --y);
    }

    static TailCall<Integer> addF(int x, int y) {
        return y == 0 ? TailCall.ret(x) : TailCall.sus(() -> addF(x + 1, y - 1));
    }

    static Function1<Integer, Function1<Integer, TailCall<Integer>>> addFF =
            x -> y -> {
                if (y == 0) {
                    return TailCall.ret(x);
                } else {
                    return TailCall.sus(() -> Chapter4.addFF.apply(x + 1).apply(y - 1));
                }
            };

    static Function2<Integer, Integer, TailCall<Integer>> addFFF =
            (x, y) -> {
                class A {

                    Function1<Integer, Function1<Integer, TailCall<Integer>>> _T =
                            a -> b -> b == 0 ?
                                    TailCall.ret(a) :
                                    TailCall.sus(
                                            () -> Chapter4.addFFF.apply(a + 1).apply(b - 1));
                }
                return new A()._T.apply(x).apply(y);
            };

    static Function1<Integer, Function0<Integer>> test = x -> {
        API.println(x);
        if (x <= 0) {
            return () -> 0;
        } else {
            return () -> Chapter4.test.apply(x - 1).apply();
        }
    };

    public static void main(String... args) {
        TailCall<Integer> tailCall = addFF.apply(10).apply(100000);
        API.print(tailCall.eval());
//        API.print(addF(10,100000).eval());
//        TailCall<Integer> tailCall = addFFF.apply(10, 100000);
//        TailCall<Integer> tailCall1 = addFFF.apply(10, 10);
//        TailCall<Integer> tailCall2 = addFFF.apply(4, 4);
//        API.print(tailCall1.eval());

//        API.println(Chapter4.test.apply(10).apply());

    }
}