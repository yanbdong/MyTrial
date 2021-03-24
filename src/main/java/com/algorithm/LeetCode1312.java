package com.algorithm;


/**
 * https://leetcode-cn.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/
 * <p>
 * 让字符串成为回文串的最少插入次数
 */
public class LeetCode1312 {

    static class Solution {

        /**
         * 我们用 dp[i][j] 表示对于字符串 s 的子串 s[i:j]（这里的下标从 0 开始，并且 s[i:j] 包含 s 中的第 i 和第 j 个字符），最少添加的字符数量，使得 s[i:j] 变为回文串。
         * <p>
         * 我们从外向内考虑 s[i:j]：
         * <p>
         * 如果 s[i] == s[j]，那么最外层已经形成了回文，我们只需要继续考虑 s[i+1:j-1]；
         * 如果 s[i] != s[j]，那么我们要么在 s[i:j] 的末尾添加字符 s[i]，要么在 s[i:j] 的开头添加字符 s[j]，才能使得最外层形成回文。如果我们选择前者，那么需要继续考虑 s[i+1:j]；如果我们选择后者，那么需要继续考虑 s[i:j-1]。
         * 因此我们可以得到如下的状态转移方程：
         * <p>
         * <p>
         * dp[i][j] = min(dp[i + 1][j] + 1, dp[i][j - 1] + 1)                     if s[i] != s[j]
         * dp[i][j] = min(dp[i + 1][j] + 1, dp[i][j - 1] + 1, dp[i + 1][j - 1])   if s[i] == s[j]
         * 边界条件为：
         * <p>
         * <p>
         * dp[i][j] = 0   if i >= j
         *
         * @return
         */
        public int minInsertions(String s) {
            final int length = s.length();
            final int lastIndex = length - 1;
            if (length <= 1) {
                return 0;
            }
            byte[] content = s.getBytes();
            // It's a right-upper triangle matrix
            // Use dp to store the prev row dp
            int[] dp = new int[length];
            // Traverse row from bottom to top
            for (int row = lastIndex; row >= 0; row--) {
                // Init prev row's row index to 0 for dp[i + 1][j - 1] needs in this round
                dp[row] = 0;
                // The new row start with dp[row][row] is 0;
                int prev = 0;
                byte startContent = content[row];
                for (int column = row + 1; column <= lastIndex; column++) {
                    int current;
                    if (startContent == content[column]) {
                        current = Math.min(Math.min(prev + 1, dp[column] + 1), dp[column - 1]);
                    } else {
                        current = Math.min(prev + 1, dp[column] + 1);
                    }
                    // The prev dp [column] value is obsoleted, thus use to save this round's value
                    dp[column - 1] = prev;
                    // Update prev value with this round's new value
                    prev = current;
                }
                // Handle the last value in this round as it won't be set in above recursion
                dp[lastIndex] = prev;
            }
            return dp[lastIndex];
        }
    }
}
