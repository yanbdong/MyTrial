package com.algorithm;

import com.google.common.collect.*;
import java.util.Collection;

public class LeetCode64 {

  class Solution {

    @SuppressWarnings("all")
    public int minPathSum(int[][] grid) {
      int row = grid.length;
      if (row == 0) {
        return 0;
      }
      int column = grid[0].length;
      if (column == 0) {
        return 0;
      }
      Collection<Integer> rowIt = ContiguousSet.create(Range.closedOpen(0, row),
          DiscreteDomain.integers());
      Collection<Integer> columbIt = ContiguousSet.create(Range.closedOpen(0, column),
          DiscreteDomain.integers());
      Table<Integer, Integer, Integer> dpTable = ArrayTable.create(rowIt, columbIt);
      // Init
      dpTable.put(0, 0, grid[0][0]);
      // row
      rowIt.stream().skip(1)
          .forEach(it -> dpTable.put(0, it, dpTable.get(0, it - 1) + grid[0][it]));
      columbIt.stream().skip(1)
          .forEach(it -> dpTable.put(it, 0, dpTable.get(it - 1, 0) + grid[it][0]));
      for (int i = 1; i < row; i++) {
        for (int j = 1; j < column; j++) {
          dpTable.put(i, j, Math.min(dpTable.get(i - 1, j), dpTable.get(i, j - 1) + grid[i][j]));
        }
      }
      return dpTable.get(row - 1, column - 1);
    }
  }
}
