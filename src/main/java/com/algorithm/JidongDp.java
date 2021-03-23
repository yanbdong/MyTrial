package com.algorithm;

import java.util.function.Function;

public class JidongDp {

  public static void main(String[] args) {
    String odd = new SolutionOdd().apply("whdvxhdbdccggddggccdbabbajdbj");
    String even = new SolutionEven().apply("whdvxhdbdccggddggccdbabbajdbj");
    System.out.println(odd);
    System.out.println(even);
  }

  /**
   * Define dp[i] as the max sub-string size between the s[i]<br/> dp[i] = (dp[i-1] + s[i] is a
   * huiwen string as well) ? dp[i-1] + 1 : 1.
   */
  public static class SolutionOdd implements Function<String, String> {

    /**
     * Consider only odd what form is ...abcbd...
     *
     * @param s
     * @return
     */
    @Override
    public String apply(String s) {
      final int size = s.length();
      if (size == 0) {
        return "";
      }
      int[] dp = new int[size];
      // Init
      dp[0] = 1;
      int max = 1;
      int maxLocation = 0;
      for (int i = 1; i < size; i++) {
        if (isHuiwenBackFromLocation(s, i, dp[i - 1])) {
          dp[i] = dp[i - 1] + 1;
        } else {
          dp[i] = 1;
        }
        if (max < dp[i]) {
          max = dp[i];
          maxLocation = i;
        }
      }
      int startIndex = maxLocation - (max-1) * 2;
      return s.substring(startIndex, maxLocation + 1);
    }

    private boolean isHuiwenBackFromLocation(String s, int i, int lastHuiwenSize) {
      int theCorrespondingHuiWenLocationToI = i - (lastHuiwenSize * 2);
      if (theCorrespondingHuiWenLocationToI < 0) {
        return false;
      }
      return s.charAt(i) == s.charAt(theCorrespondingHuiWenLocationToI);
    }
  }

  /**
   * Define dp[i] as the max sub-string size between the s[i]<br/> dp[i] = (dp[i-1] + s[i] is a
   * huiwen string as well) ? dp[i-1] + 1 : 1.
   */
  public static class SolutionEven implements Function<String, String> {

    /**
     * Consider only even what form is ...abbd...
     *
     * @param s
     * @return
     */
    @Override
    public String apply(String s) {
      final int size = s.length();
      if (size <= 1) {
        return "";
      }
      int[] dp = new int[size];
      // Init
      dp[0] = 0;
      int max = 0;
      int maxLocation = 0;
      for (int i = 1; i < size; i++) {
        if (dp[i - 1] == 0) {
          if (s.charAt(i) == s.charAt(i - 1)) {
            dp[i] = 1;
          } else {
            dp[i] = 0;
          }
        } else {
          if (isHuiwenBackFromLocation(s, i, dp[i - 1])) {
            dp[i] = dp[i - 1] + 1;
          } else {
            dp[i] = 0;
          }
        }
        if (max < dp[i]) {
          max = dp[i];
          maxLocation = i;
        }
      }
      int startIndex = maxLocation - (max * 2 - 1);
      return s.substring(startIndex, maxLocation + 1);
    }

    private boolean isHuiwenBackFromLocation(String s, int i, int lastHuiwenSize) {
      int theCorrespondingHuiWenLocationToI = i - (lastHuiwenSize * 2 + 1);
      if (theCorrespondingHuiWenLocationToI < 0) {
        return false;
      }
      return s.charAt(i) == s.charAt(theCorrespondingHuiWenLocationToI);
    }
  }
}
