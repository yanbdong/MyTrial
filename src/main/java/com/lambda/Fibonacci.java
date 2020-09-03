package com.lambda;

import java.math.BigInteger;

import io.vavr.API;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Fibonacci {

    int normal(int number) {
        return Match(number).of(
                Case($(0), number),
                Case($(1), number),
                Case($(), normal(number - 1) + normal(number - 2)));
    }

    int fib_(int acc1, int acc2, int number) {
        return Match(number).of(
                Case($(0), 0),
                Case($(1), acc1 + acc2),
                Case($(), fib_(acc2, acc1 + acc2, number - 1)));
    }

    Chapter4.TailCall<BigInteger> stackSafeFib_(BigInteger acc1, BigInteger acc2, int number) {
        return Match(number).of(
                Case($(0), Chapter4.TailCall.ret(BigInteger.ZERO)),
                Case($(1), Chapter4.TailCall.ret(acc1.add(acc2))),
                Case($(), Chapter4.TailCall.sus(() -> stackSafeFib_(acc2, acc1.add(acc2), number - 1))));
    }

    BigInteger stackSafeFib(int number) {
        return stackSafeFib_(BigInteger.ONE, BigInteger.ZERO, number).eval();
    }

    public static void main(String... args) {
        BigInteger integer = new Fibonacci().stackSafeFib(10000);
        API.println(integer);
        API.println(String.valueOf(integer).length());
    }
}