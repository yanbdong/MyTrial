package com.algorithm;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/n-queens/
 * <p>
 * n 皇后问题
 */
public class LeetCode51 {

    static class Solution {


        public List<List<String>> solveNQueens(int n) {
            List<String> sTemp = new ArrayList<>();
            char[] dots = new char[n];
            Arrays.fill(dots, '.');
            for (int i = 0; i < n; i++) {
                dots[i] = 'Q';
                sTemp.add(new String(dots));
                dots[i] = '.';
            }
            List<List<String>> ret = new ArrayList<>();
            recursive(0, new int[n], new String[n], ret, sTemp);
            return ret;
        }

        /**
         * @param round    the row current working on
         * @param location The location in column each row takes
         */
        private void recursive(int round, int[] location, String[] allPrevs, List<List<String>> ret, List<String> sTemp) {
            int length = location.length;
            // Available column for this row
            TAG:
            // Try all columns
            for (int availableLocation = 0; availableLocation < length; availableLocation++) {
                // Find available location
                for (int l = 0; l < round; l++) {
                    int usedInLstRow = location[l];
                    // Check vertical direction
                    if (availableLocation == usedInLstRow) {
                        continue TAG;
                    }
                    int diffDistance = round - l;
                    // Check left slash direction
                    int toDetectInLeftSlashRow = availableLocation + diffDistance;
                    if (toDetectInLeftSlashRow < length && toDetectInLeftSlashRow == usedInLstRow) {
                        continue TAG;
                    }
                    // Check right slash direction
                    int toDetectInRightSlashRow = availableLocation - diffDistance;
                    if (toDetectInRightSlashRow >= 0 && toDetectInRightSlashRow == usedInLstRow) {
                        continue TAG;
                    }
                }
                allPrevs[round] = sTemp.get(availableLocation);
                if (round == length - 1) {
                    // Print
                    List<String> temp = new ArrayList<>(length);
                    for (String s : allPrevs) {
                        temp.add(s);
                    }
                    ret.add(temp);
                    return;
                } else {
                    location[round] = availableLocation;
                    recursive(round + 1, location, allPrevs, ret, sTemp);
                }
            }
        }
    }
}
