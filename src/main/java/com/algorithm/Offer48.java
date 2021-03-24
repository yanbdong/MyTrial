package com.algorithm;

/**
 * https://leetcode-cn.com/problems/zui-chang-bu-han-zhong-fu-zi-fu-de-zi-zi-fu-chuan-lcof/
 * 请从字符串中找出一个最长的不包含重复字符的子字符串，计算该最长子字符串的长度。
 */
public class Offer48 {

    static class Solution {


        /**
         * 1. 定义一个一维数组，dp[i]表示：以s[i]为结尾的的所有不包含重复字符的子字符串中，最长的值。
         * 2. 转移方程定义为：dp[i] = s[i]是否存在与 前dp[i-1]个字符（这个肯定不重复）中 ？ 从前dp[i-1]个字符那个重复的位置之后，计算新的值 : dp[i-1] + 1
         *
         * @return
         */
        public int lengthOfLongestSubstringMore(String s) {
            if (s.isEmpty()) {
                return 0;
            }
            byte[] content = s.getBytes();
            int[] dp = new int[s.length()];
            // 初始
            dp[0] = 1;
            int max = 1;
            for (int i = 1; i < s.length(); i++) {
                int lookBack;
                byte temp = content[i];
                for (lookBack = 1; lookBack <= dp[i - 1]; lookBack++) {
                    if (temp == content[i - lookBack]) {
                        break;
                    }
                }
                dp[i] = lookBack;
                max = Math.max(max, lookBack);
            }
            return max;
        }
        public int lengthOfLongestSubstring(String s) {
            if (s.isEmpty()) {
                return 0;
            }
            byte[] content = s.getBytes();
            int dpPrev = 1;
            int max = 1;
            for (int i = 1; i < s.length(); i++) {
                int lookBack;
                byte temp = content[i];
                for (lookBack = 1; lookBack <= dpPrev; lookBack++) {
                    if (temp == content[i - lookBack]) {
                        break;
                    }
                }
                dpPrev = lookBack;
                max = Math.max(max, lookBack);
            }
            return max;
        }
    }
}
