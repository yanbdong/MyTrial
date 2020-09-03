package com.lambda;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.control.Try;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

/**
 * @author yanbdong@cienet.com.cn
 * @since Apr 11, 2019
 */
public class Test {

    public static void main(String... args) {
        System.out.println("In test");
        Try<String> lines = Try.of(() -> Files.readAllLines(Paths.get("1.txt")))
                .map(list -> String.join(",", list))
                .andThen((Consumer<String>) System.out::println);
        System.out.println(lines);
        String input = "g";
        String result = Match(input).of(
                Case($("g"), "good"),
                Case($("b"), "bad"),
                Case($(), "unknown")
        );
        Function<B, B> function = x -> x;
        Function<A, C> function1 = x -> (C) x;
        Function<E, C> function3 = x -> x;


        Function<C, B> function2 = function.compose(function1);


//        M<Month> month = null;
//        M<Integer> dayOfMonth = null;
//        int x = 0;
//        M<LocalDate> t = (M<LocalDate>) dayOfMonth.map(it -> LocalDate.of(2016, x, it));
//
//        M<LocalDate> date = (M<LocalDate>) month.flatMap((Month m) ->
//                );
        IBinaryOperator<Integer> add = a -> b -> a + b;
        add.apply(1).apply(2);
        API.Function((A x1, B x2, C x3, D x4) -> 0).curried().apply(new A()).apply(null).apply(null).apply(null);

        IBinaryOperator<Function1<Integer, Integer>> composeFunction = y1 -> y2 -> z -> y1.apply(y2.apply(z));
        IBinaryOperator<Function1<Integer, Integer>> composeFunction1 = y1 -> y1::compose;
        Function1<Integer, Integer> f1 = a -> a + 1;
        Function1<Integer, Integer> f2 = a -> a * 5;
        Function1<Integer, Integer> f3 = composeFunction.apply(f1).apply(f2);
        Function1<Integer, Integer> f4 = composeFunction1.apply(f1).apply(f2);
        System.out.println(f3.apply(7));
        System.out.println(f4.apply(7));

        Double cos = Function1.<Double, Double>of(x -> Math.PI / 2 - x).compose(Math::sin).apply(2.0);
        Double cos1 = API.Function((Double x) -> Math.PI / 2 - x).compose(Math::sin).apply(2.0);

    }

    public Function<Integer, Integer> factorial = n -> n <= 1 ? n : n * this.factorial.apply(n - 1);

    <T1, T2, T3, T4, T5, T6, R> Function5<T1, T2, T3, T4, T6, R> partialT5(T5 t5, Function6<T1, T2, T3, T4, T5, T6, R> f) {
        return (t1, t2, t3, t4, t6) -> f.apply(t1, t2, t3, t4, t5, t6);
    }

    interface IBinaryOperator<T> extends Function1<T, Function1<T, T>> {

    }

    static A function(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return new A();
    }

    static class BinaryOperator implements IBinaryOperator<Integer> {

        @Override
        public Function1<Integer, Integer> apply(Integer integer) {
            return null;
        }
    }

//    interface Functor<T, F extends Functor<?, ?>> {
//
//        <R> F map(Function<T, R> f);
//    }
//
//    static class F<T> implements Functor<T, F<?>> {
//
//        public F(T data) {
//            mData = data;
//        }
//
//        private T mData;
//
//        @Override
//        public <R> F<R> map(Function<T, R> f) {
//            R tmp = f.apply(mData);
//            return new F<>(tmp);
//        }
//    }
//
//    interface Monad<T, M extends Monad<?, ?>> extends Functor<T, M> {
//
//        M flatMap(Function<T, M> f);
//    }
//
//    static class M<T> implements Monad<T, M<?>> {
//
//        public M(T data) {
//            mData = data;
//        }
//
//        private T mData;
//
//        @Override
//        public <R> M<?> map(Function<T, R> f) {
//            R tmp = f.apply(mData);
//            return new M<>(tmp);
//        }
//
//        @Override
//        public M<?> flatMap(Function<T, M<?>> f) {
//            return f.apply(mData);
//        }
//    }

    static class A1 {

    }

    static class A2 {

    }

    static class A3 {

    }

    static class A4 {

    }

    static class A5 {

    }

    static class A6 {

    }

    static class A {

    }

    static class B extends A {

    }

    static class C extends B {

    }

    static class D extends C {

    }

    static class E extends D {

    }
}
