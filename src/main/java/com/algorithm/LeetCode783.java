package com.algorithm;


import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode-cn.com/problems/n-ary-tree-preorder-traversal/submissions/
 * <p>
 * 给定一个 N 叉树，返回其节点值的 前序遍历 。
 */
public class LeetCode783 {


    public static void main(String[] args) {
        TreeNode root12 = new TreeNode(12);
        TreeNode root49 = new TreeNode(49);
        TreeNode root48 = new TreeNode(49, root12, root49);
        TreeNode root0 = new TreeNode(0);
        TreeNode root = new TreeNode(1, root0, root48);
        new Solution().minDiffInBST(root);
    }

    public static class TreeNode {

        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {}

        TreeNode(int val) { this.val = val; }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    static class Solution {

        private Map<TreeNode, Tuple> record = new HashMap<>();

        public int minDiffInBST(TreeNode root) {
            return findMin(root);
        }

        private int findMin(TreeNode root) {
            int leftDiff;
            if (root.left == null) {
                leftDiff = Integer.MAX_VALUE;
            } else {
                int maxValueInLeftTree = findMaxNode(root.left);
                leftDiff = root.val - maxValueInLeftTree;
                if (leftDiff == 0) {
                    return 0;
                }
                int childMin = findMin(root.left);
                if (childMin == 0) {
                    return 0;
                }
                leftDiff = Math.min(leftDiff, childMin);
            }

            int rightDiff;
            if (root.right == null) {
                rightDiff = Integer.MAX_VALUE;
            } else {
                int minValueInRightTree = findMinNode(root.right);
                rightDiff = minValueInRightTree - root.val;
                if (rightDiff == 0) {
                    return 0;
                }
                int childMin = findMin(root.right);
                if (childMin == 0) {
                    return 0;
                }
                rightDiff = Math.min(rightDiff, childMin);

            }
            return Math.min(leftDiff, rightDiff);
        }

        private int traverseChild(TreeNode root, TreeNode child) {
            if (child == null) {
                return Integer.MAX_VALUE;
            }
            int current = Math.abs(root.val - child.val);
            if (current == 0) {
                return 0;
            }
            int childMin = findMin(child);
            if (childMin == 0) {
                return 0;
            }
            return Math.min(current, childMin);
        }

        private int findMaxNode(TreeNode root) {
            if (root == null) {
                return -1;
            }
            Tuple tuple = record.get(root);
            if (tuple != null && tuple.max != -1) {
                return tuple.max;
            }
            int max = findMaxNode(root.right);
            record.computeIfAbsent(root, it -> new Tuple());
            if (max == -1) {
                record.get(root).max = root.val;
                return root.val;
            } else {
                record.get(root).max = max;
                return max;
            }
        }

        private int findMinNode(TreeNode root) {
            if (root == null) {
                return -1;
            }
            Tuple tuple = record.get(root);
            if (tuple != null && tuple.min != -1) {
                return tuple.min;
            }
            int min = findMinNode(root.left);
            record.computeIfAbsent(root, it -> new Tuple());
            if (min == -1) {
                record.get(root).min = root.val;
                return root.val;
            } else {
                record.get(root).min = min;
                return min;
            }
        }

        private static class Tuple {

            int min = -1, max = -1;
        }
    }
}
