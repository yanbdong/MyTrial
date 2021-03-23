package com.algorithm;

import java.util.function.Function;

public class Jidong {

  public static void main(String[] args) {
    new Solution().apply("whdvxhdbdccggddggccdbabbajdbj");
  }

  /**
   * Define dp[i][j] as the max sub-string size from index i from j.<br/> Thus if
   */
  public static class Solution implements Function<String, String> {

    @Override
    public String apply(String s) {
      int max = 0;
      for (int i = 0; i<s.length(); i++) {
        // For location at i
        int temp = Math.max(findOdd(s,i), findEven(s,i));
        max = Math.max(temp,max);
      }
      return "max";
    }

    /**
     * Find the max sub-string size at given location<br/>
     * Consider the case what is ...abcba..
     */
    private int findOdd(String s, int location) {
      int left = location - 1, right = location + 1;
      int ret = 0;
      while (left >= 0 && right < s.length()) {
        if (s.charAt(left) == s.charAt(right)) {
          ret++;
          left--;
          right++;
        } else {
          break;
        }
      }
      return ret;
    }

    /**
     * Find the max sub-string size at given location<br/>
     * Consider the case what is ...abba..
     */
    private int findEven(String s, int location) {
      int left = location - 1, right = location + 1;
      if (left >= 0 && s.charAt(left) == s.charAt(location)) {
        left--;
      } else if (right < s.length() && s.charAt(right) == s.charAt(location)) {
        right++;
      } else {
        return 0;
      }
      // start with 1
      int ret = 1;
      while (left >= 0 && right < s.length()) {
        if (s.charAt(left) == s.charAt(right)) {
          ret++;
          left--;
          right++;
        } else {
          break;
        }
      }
      return ret;
    }
  }

}
