package com.algorithm;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * The all parentheses combination
 */
public class Parentheses implements Consumer<Integer> {

  @Override
  public void accept(Integer integer) {
    recursive(Node.start(integer));
  }

  private void recursive(Node node) {
    if (node == null) {
      // trim
      return;
    }
    if (node.canContinue()) {
      recursive(node.enterLeft());
      recursive(node.enterRight());
    } else {
      node.print();
    }
  }

  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  @AllArgsConstructor
  private static class Node {

    Node prev;
    boolean stepLeft;
    int availableLeftParentheses;
    int availableRightParentheses;


    static Node start(int n){
      return new Node(null, true, n-1, 1);
    }

    Node enterLeft(){
      if (this.availableLeftParentheses == 0) {
        return null;
      } else {
        return new Node(this, true, this.availableLeftParentheses - 1,
            this.availableRightParentheses + 1);
      }
    }

    Node enterRight(){
      if (this.availableRightParentheses == 0) {
        return null;
      } else {
        return new Node(this, false, this.availableLeftParentheses,
            this.availableRightParentheses - 1);
      }
    }

    boolean canContinue() {
      return availableRightParentheses != 0 || availableLeftParentheses != 0;
    }

    public void print() {
      Node current = this;
      StringBuilder sb = new StringBuilder();
      while (current != null) {
        sb.append(current.stepLeft ? '(' : ')');
        current = current.prev;
      }
      System.out.println(sb.reverse());
    }
  }
}
