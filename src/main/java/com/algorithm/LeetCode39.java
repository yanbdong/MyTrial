package com.algorithm;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/combination-sum/comments/
 * <p>
 * 组合总和
 */
public class LeetCode39 {

    static class Solution {

        public List<List<Integer>> combinationSum(int[] candidates, int target) {
            Arrays.sort(candidates);
            List<List<Integer>> ret = new ArrayList<>();
            recursive(candidates, target, 0, 0, new ArrayList<>(), -1, ret);
            return ret;
        }

        /**
         * @return trim steps
         */
        private void recursive(int[] candidates, int target, int currentSum, int currentIndex, List<Integer> temp, int tempTopIndex, List<List<Integer>> ret) {
            // Sub nodes start with all the values which are greater than prev used value to avoid duplication
            for (int i = currentIndex; i < candidates.length; i++) {
                int currentValue = candidates[i];
                int sum = currentSum + currentValue;
                if (sum > target) {
                    // Trim left sibling
                    return;
                }
                int newIndex = tempTopIndex + 1;
                if (temp.size() == newIndex) {
                    temp.add(currentValue);
                } else {
                    temp.set(newIndex, currentValue);
                }
                if (sum == target) {
                    List<Integer> t = new ArrayList<>();
                    for (int j = 0; j <= newIndex; j++) {
                        t.add(temp.get(j));
                    }
                    ret.add(t);
                    // Trim left sibling
                    return;
                }
                recursive(candidates, target, sum, i, temp, newIndex, ret);
            }
        }
    }
}
