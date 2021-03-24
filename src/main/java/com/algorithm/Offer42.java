package com.algorithm;


public class Offer42 {

    static class Solution {

        /**
         * dp[i][j] presents the sum of subList from index i to j.
         * dp[i][j] = max((dp[i+1][j], dp[i][j-1], sum(i,j))
         * The edge is: It's a two-dimension upper-right triangular matrix
         *
         * @param nums
         * @return
         */
        public int maxSubArray0(int[] nums) {
            final int size = nums.length;
            if (size == 0) {
                return 0;
            }
            // Use to store max subList value from i to j
            int dp[] = new int[size];
            // Use to store sum from i to j
            int sum[] = new int[size];
            // Init
            for (int row = size - 1; row >= 0; row--) {
                // The first element on the same row
                int dpPrev = nums[row];
                int sumPrev = nums[row];
                for (int column = row + 1; column <= size - 1; column++) {
                    int sumCurrent = sumPrev + nums[column];
                    int dpCurrent = Math.max(Math.max(dpPrev, dp[column]), sumCurrent);
                    dp[column - 1] = dpPrev;
                    sum[column - 1] = sumPrev;
                    dpPrev = dpCurrent;
                    sumPrev = sumCurrent;
                }
                dp[size - 1] = dpPrev;
                sum[size - 1] = sumPrev;
            }
            return dp[size - 1];
        }


        /**
         1. 定义一个一维数组，dp[i]表示：以A[i]为结尾的的所有子数组中最大的那个。
         2. 转移方程定义为：dp[i] = dp[i-1] <= 0 ？ A[i] : dp[i-1] + A[i]
         即如果前面一个dp[i-1]为负数了，那么前面的结果都不要了，直接从当下这个位置开始一个新的子数组吧
         *
         * @param nums
         * @return
         */
        public int maxSubArray(int[] nums) {
            final int size = nums.length;
            if (size == 0) {
                return 0;
            }
            // Use to store max subList value from i to j
            int dp[] = new int[size];
            // Use to store sum from i to j
            int sum[] = new int[size];
            // Init
            for (int row = size - 1; row >= 0; row--) {
                // The first element on the same row
                int dpPrev = nums[row];
                int sumPrev = nums[row];
                for (int column = row + 1; column <= size - 1; column++) {
                    int sumCurrent = sumPrev + nums[column];
                    int dpCurrent = Math.max(Math.max(dpPrev, dp[column]), sumCurrent);
                    dp[column - 1] = dpPrev;
                    sum[column - 1] = sumPrev;
                    dpPrev = dpCurrent;
                    sumPrev = sumCurrent;
                }
                dp[size - 1] = dpPrev;
                sum[size - 1] = sumPrev;
            }
            return dp[size - 1];
        }
    }
}
