package com.algorithm;


import java.util.Arrays;
import java.util.Stack;

/**
 * https://leetcode-cn.com/problems/smallest-subsequence-of-distinct-characters/
 * <p>
 * 返回 s 字典序最小的子序列，该子序列包含 s 的所有不同字符，且只包含一次
 */
public class LeetCode1081 {

    static class Solution {

        public String smallestSubsequence(String s) {
            if (s.isEmpty()) {
                return s;
            }
            int[] cardinal = new int[26];
            Arrays.fill(cardinal, 0);
            byte[] content = s.getBytes();
            // Make cardinal counts
            for (byte b : content) {
                cardinal[b - 'a'] += 1;
            }
            boolean[] inUsed = new boolean[26];
            Arrays.fill(inUsed, false);
            //
            Stack<Byte> stack = new Stack<>();
            stack.push(content[0]);
            cardinal[content[0] - 'a'] -= 1;
            inUsed[content[0] - 'a'] = true;
            //
            TAG:
            for (int index = 1; index < content.length; index++) {
                byte temp = content[index];
                int tempIndex = temp - 'a';
                cardinal[tempIndex] -= 1;
                // If existed before, just discard
                if (inUsed[tempIndex]) {
                    continue;
                }
                // Find the best place in the stack
                while (!stack.isEmpty()) {
                    byte peek = stack.peek();
                    if (peek < temp) {
                        // If greater, just follow prev
                        stack.push(temp);
                        inUsed[tempIndex] = true;
                        continue TAG;
                    } else if (cardinal[peek - 'a'] == 0) {
                        // If prev has no more later, just follow
                        stack.push(temp);
                        inUsed[tempIndex] = true;
                        continue TAG;
                    } else {
                        // The current can be discarded.
                        stack.pop();
                        inUsed[peek - 'a'] = false;
                    }
                }
                // Stack is empty
                stack.push(temp);
                inUsed[tempIndex] = true;
            }
            byte[] bytes = new byte[stack.size()];
            for (int i = 0; i < stack.size(); i++) {
                bytes[i] = stack.get(i);
            }
            return new String(bytes);
        }
    }
}
