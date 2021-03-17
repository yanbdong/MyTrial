package com.qidian;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;

/**
 * Given an array of integers, find the maximum sum of non-empty subarray with at most one deletion.
 * In other words, you can choose a subarray and optionally delete one element from it and there must be at least one element left in the subarray.
 *
 * @author yanbdong@cienet.com.cn
 * @since Mar 16, 2021
 */
public class Question2 implements Function<List<Integer>, Integer> {


    @Override
    public @NotNull Integer apply(@NotNull List<Integer> integers) {
        final int size = integers.size();
        final int subSetCount = (int) Math.pow(2, size);
        // Bypass the case which size is 0/1/2
        // Bypass the full set which looks like 111111111....
        return io.vavr.collection.List.range(3, subSetCount - 1)
                .filter(it -> countBits(it, size) >= 2)
                .map(it -> collectIntoSubSet(it, integers))
                .collect(Collectors.maxBy(Comparator.comparingInt(SubSet::getMaxComposition)))
                // Should be not null
                .get()
                .getMaxComposition();
    }

    /**
     * Count how many 1 in binary former
     *
     * @param highIndex The most top bit
     */
    private int countBits(Integer number, int highIndex) {
        final int target = number;
        int sum = 0;
        for (int index = 0; index < highIndex; index++) {
            sum += (number >> index) & 1;
        }
        return sum;
    }

    private SubSet collectIntoSubSet(Integer number, List<Integer> array) {
        SubSet subset = new SubSet();
        for (int index = 0; index < array.size(); index++) {
            if ((((number >> index) & 1)) == 1) {
                subset.add(array.get(index));
            }
        }
        return subset;
    }


    /**
     * To store the subset.
     */
    public static class SubSet {

        private final List<Integer> elements = new ArrayList<>();
        private Integer min = Integer.MAX_VALUE;

        SubSet add(Integer element) {
            elements.add(element);
            if (element < min) {
                min = element;
            }
            return this;
        }

        int getMaxComposition() {
            int sum = elements.stream().mapToInt(Integer::intValue).sum();
            // Optional remove
            if (min <= 0) {
                sum -= min;
            }
            return sum;
        }
    }
}
