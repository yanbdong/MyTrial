package com.algorithm;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://leetcode-cn.com/problems/n-ary-tree-preorder-traversal/submissions/
 * <p>
 * 给定一个 N 叉树，返回其节点值的 前序遍历 。
 */
public class LeetCode589 {

    static Node cons() {
        List<Node> list = IntStream.range(1, 7).mapToObj(Node::new).collect(Collectors.toList());
        list.get(2).add(list.get(4)).add(list.get(5));
        list.get(0).add(list.get(2)).add(list.get(1)).add(list.get(3));
        return list.get(0);

    }

    static class Node {

        public int val;
        public List<Node> children = new ArrayList<>();

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }

        public Node add(Node child) {
            children.add(child);
            return this;
        }
    }

    static class Solution {

        public List<Integer> preorder(Node root) {
            List<Integer> ret = new ArrayList<>();
            Stack<Node> stack = new Stack<>();
            // To record the child index which has been traversed.
            Stack<Integer> childIndexStack = new Stack<>();
            Node currentNode = root;
            do {
                if (null == currentNode) {
                    while (!stack.isEmpty()) {
                        Node parent = stack.peek();
                        int nextChildIndex = childIndexStack.pop() + 1;
                        if (nextChildIndex < parent.children.size()) {
                            // Next child
                            currentNode = parent.children.get(nextChildIndex);
                            childIndexStack.push(nextChildIndex);
                            break;
                        } else {
                            // This node has traversed all its child
                            stack.pop();
                        }
                    }
                } else {
                    // preorder
                    ret.add(currentNode.val);
                    // Push into stack
                    stack.add(currentNode);
                    childIndexStack.add(0);
                    //
                    currentNode = currentNode.children.isEmpty() ? null : currentNode.children.get(0);
                }
            } while (!stack.isEmpty());
            return ret;
        }
    }

    static class Solution2 {

        public List<Integer> preorder(Node root) {
            List<Integer> ret = new ArrayList<>();
            Stack<Node> stack = new Stack<>();
            stack.push(root);
            do {
                Node c = stack.pop();
                ret.add(c.val);
                for (int i = c.children.size() - 1; i >= 0; i--) {
                    stack.push(c.children.get(i));
                }

            } while (stack.isEmpty());
            return ret;
        }
    }
}
