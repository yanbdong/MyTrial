package com.lambda;

import io.vavr.control.Try;

/**
 * @author yanbdong@cienet.com.cn
 * @since Jun 18, 2020
 */
class Chapter7 {

    static String cc(Integer i1, Float i2, Double i3, Long i4, Boolean i5) {
        return "sb";
    }

    public static void main(String... args) {
        Try<Integer> try1 = Try.success(1);
        Try<Float> try2 = Try.success(2F);
        Try<Double> try3 = Try.success(3D);
        Try<Long> try4 = Try.success(4L);
        Try<Boolean> try5 = Try.success(true);
        /*
         * comprehension
         */
        Try<String> stringTry = try1.flatMap(p1 -> try2
                .flatMap(p2 -> try3.flatMap(p3 -> try4.flatMap(p4 -> try5.map(p5 -> cc(p1, p2, p3, p4, p5))))));
    }
}