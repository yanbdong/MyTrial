package com.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 第二题：设计和构建一个“最近最少使用”缓存
 *
 * @author yanbdong
 * @since 2021/03/38
 */
public class JingDong<Key> {

  /**
   * 最大容量
   */
  private final int size;
  /**
   * 使用map来存储数据
   */
  private Map<Key, Node<Key>> dataCollection;
  /**
   * 使用链表记录最近最少使用的结点
   */
  private final Node<Key> head;
  private final Node<Key> tail;

  public JingDong(int size) {
    this.size = size;
    dataCollection = new HashMap<>((int) (size / 0.75) + 1);
    // 初始化双链表
    head = new Node<>();
    tail = new Node<>();
    head.next = tail;
    tail.next = head;
  }

  public Integer get(Key key) {
    Node<Key> target = dataCollection.get(key);
    if (null == target) {
      return -1;
    }
    // 更新链表，将该结点的访问提前
    target.removeFromList();
    target.insertAfter(head.prev);
    return target.value;
  }

  public void put(Key key, Integer value) {
    if (dataCollection.size() >= this.size - 1) {
      // 缓存块爆了，先删除
      Node<Key> toBeRemove = tail.prev;
      toBeRemove.removeFromList();
      dataCollection.remove(toBeRemove.key);
    }
    Node<Key> newNode = new Node<>();
    newNode.key = key;
    newNode.value = value;
    newNode.insertAfter(head);
  }

  /**
   * 一个双向结点
   *
   * @param <Key>
   */
  static class Node<Key> {

    Key key;
    Integer value;
    Node<Key> prev;
    Node<Key> next;

    /**
     * 从链表中摘除该结点。修改前后指针关系
     */
    void removeFromList() {
      this.prev = this.next;
      this.next = this.prev;
    }

    /**
     * 插入到给定元素后面
     *
     * @param prev
     */
    void insertAfter(Node<Key> prev) {
      Node<Key> formerNext = prev.next;
      prev.next = this;
      formerNext.prev = this;
      this.prev = prev;
      this.next = formerNext;
    }
  }
}
