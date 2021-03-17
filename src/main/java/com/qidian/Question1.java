package com.qidian;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Given an array of integers, find all pairs of two elements that add up to a specific target number.
 * The results should be distinct and elements in each result pair should be in ascending order.
 *
 * @author yanbdong@cienet.com.cn
 * @since Mar 16, 2021
 */
public class Question1 implements BiFunction<List<Integer>, Integer, List<Question1.Pair>> {

    /**
     * If we can repeatedly choose the same element to construct a pair?
     */
    @Override
    public @NotNull List<Pair> apply(@NotNull List<Integer> integers, @NotNull Integer target) {
        Set<Pair> pairSet = new HashSet<>();
        final int size = integers.size();
        for (int mainIndex = 0; mainIndex < size - 1; mainIndex++) {
            Integer first = integers.get(mainIndex);
            for (int cursor = mainIndex + 1; cursor < size; cursor++) {
                Integer second = integers.get(cursor);
                // Sum compares with target
                if ((first + second) == target) {
                    pairSet.add(Pair.build(first, second));
                }
            }
        }
        return new ArrayList<>(pairSet);
    }

    /**
     * To store the result.</br>
     * Override {@linkplain #equals(Object)} and {@linkplain #hashCode()} to be identity in the {@linkplain Set}
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Pair {

        Integer _1;
        Integer _2;

        /**
         * Ensure ascending order
         */
        public static Pair build(Integer first, Integer second) {
            return first <= second ? new Pair(first, second) : new Pair(second, first);
        }

        @Override
        public String toString() {
            return "{" + _1 + ", " + _2 + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair pair = (Pair) o;
            return Objects.equals(_1, pair._1) && Objects.equals(_2, pair._2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_1, _2);
        }
    }

}
