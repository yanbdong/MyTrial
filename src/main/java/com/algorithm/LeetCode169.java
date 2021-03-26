package com.algorithm;


/**
 * https://leetcode-cn.com/problems/majority-element/
 * <p>
 * 多数元素
 */
public class LeetCode169 {

    static class Solution {

        public int majorityElement(int[] nums) {
            int crown = nums[0];
            // The crown number;
            int number = 1;
            for (int i = 1; i < nums.length; i++) {
                int defier = nums[i];
                if (crown == defier) {
                    number++;
                } else {
                    number--;
                    if (number == 0) {
                        // No IndexOutOfBoundary worry
                        i++;
                        // new king
                        crown = nums[i];
                        number++;
                    }
                }
            }
            return crown;
        }
    }
}
