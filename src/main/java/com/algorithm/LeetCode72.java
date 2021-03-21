package com.algorithm;


import java.nio.charset.StandardCharsets;

public class LeetCode72 {

  static class Solution {

    public int minDistance(String word1, String word2) {
      byte[] c1 = word1.getBytes(StandardCharsets.UTF_8);
      byte[] c2 = word2.getBytes(StandardCharsets.UTF_8);
      int row = word1.length() + 1;
      int column = word2.length() + 1;
      int[] prevRow = new int[column];
      prevRow[0] = 0;
      for (int index = 1; index < column; index++) {
        prevRow[index] = prevRow[index - 1] + 1;
      }
      int prevColumn, current;
      for (int rowIndex = 1; rowIndex < row; rowIndex++) {
        prevColumn = prevRow[0] + 1;
        for (int columnIndex = 1; columnIndex < column; columnIndex++) {
          if (c1[rowIndex - 1] == c2[columnIndex - 1]) {
            current = prevRow[columnIndex - 1];
          } else {
            current =
                Math.min(Math.min(prevColumn, prevRow[columnIndex]), prevRow[columnIndex - 1]) + 1;
          }
          prevRow[columnIndex - 1] = prevColumn;
          prevColumn = current;
        }
        prevRow[column - 1] = prevColumn;
      }
      return prevRow[column - 1];
    }
  }
}
