package com.algorithm;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/longest-substring-with-at-least-k-repeating-characters/
 * <p>
 * 给你一个字符串 s 和一个整数 k ，请你找出 s 中的最长子串， 要求该子串中的每一字符出现次数都不少于 k 。返回这一子串的长度。
 */
public class LeetCode395 {

    public static void main(String[] args) {
        new Solution().longestSubstring("aaabb", 3);
    }

    static class Solution {

        /**
         * no overlap sub-question. Thus division and conquer.
         *
         * @param s
         * @param k
         * @return
         */
        public int longestSubstring(String s, int k) {
            byte[] content = s.getBytes();
            for (int i = 0; i < content.length; i++) {
                content[i] -= 'a';
            }
            return divide0(content, k);
        }

        private int divide(byte[] content, int k, int startIncludeIndex, int endExcludeIndex) {
            if ((endExcludeIndex - startIncludeIndex) < k) {
                return 0;
            }
            int[] cardinal = new int[26];
            Arrays.fill(cardinal, 0);
            // Make cardinal counts
            for (int index = startIncludeIndex; index < endExcludeIndex; index++) {
                cardinal[content[index]] += 1;
            }
            boolean existLengthGreaterThanK = false;
            for (int c : cardinal) {
                if (c >= k) {
                    existLengthGreaterThanK = true;
                    break;
                }
            }
            if (!existLengthGreaterThanK) {
                return 0;
            }
            int max = 0;
            int start = startIncludeIndex;
            for (int index = startIncludeIndex; index < endExcludeIndex; index++) {
                if (cardinal[content[index]] < k) {
                    // Find the division point
                    max = Math.max(max, divide(content, k, start, index + 1));
                    start = index + 1;
                }
            }
            return max;
        }


        private int divide0(byte[] content, int k) {
            int[] cardinal = new int[26];
            List<Tuple> list = new LinkedList<>();
            list.add(new Tuple(0, content.length));
            int max = 0;
            int xx = 0;
            while (xx < list.size()) {
                Tuple tuple = list.get(xx);
                xx ++;
//                list.remove(0);
                if ((tuple.endExcludeIndex - tuple.startIncludeIndex) < Math.max(max, k)) {
                    continue;
                }
                Arrays.fill(cardinal, 0);  // Make cardinal counts
                for (int index = tuple.startIncludeIndex; index < tuple.endExcludeIndex; index++) {
                    cardinal[content[index]] += 1;
                }
                boolean existLengthGreaterThanK = false;
                boolean isAllGreaterThanK = true;
                for (int c : cardinal) {
                    if (c == 0) {
                        continue;
                    }
                    if (isAllGreaterThanK && c < k) {
                        isAllGreaterThanK = false;
                    }
                    if (!existLengthGreaterThanK && c >= k) {
                        existLengthGreaterThanK = true;
                    }
                    if (!isAllGreaterThanK && existLengthGreaterThanK) {
                        break;
                    }
                }
                if (!existLengthGreaterThanK) {
                    continue;
                }
                if (isAllGreaterThanK) {
                    max = Math.max(max, tuple.endExcludeIndex - tuple.startIncludeIndex);
                    continue;
                }
                int start = tuple.startIncludeIndex;
                for (int index = tuple.startIncludeIndex; index < tuple.endExcludeIndex; index++) {
                    if (cardinal[content[index]] < k) {
                        // Find the division point
                        list.add(new Tuple(start, index));
                        start = index;
                    }
                }
                list.add(new Tuple(start, tuple.endExcludeIndex));
            }
            return max;
        }


        private static class Tuple {

            final int startIncludeIndex;
            final int endExcludeIndex;

            private Tuple(int startIncludeIndex, int endExcludeIndex) {
                this.startIncludeIndex = startIncludeIndex;
                this.endExcludeIndex = endExcludeIndex;
            }
        }
    }
}
