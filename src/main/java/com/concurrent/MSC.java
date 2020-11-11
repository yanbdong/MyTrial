package com.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 20, 2020
 */
class MSC {

    static class Node {

        volatile boolean locked;

        volatile Node next;

        Node unlock() {
            locked = false;
            return this;
        }

        Node lock() {
            locked = true;
            return this;
        }
    }

    private AtomicReference<Node> mTail = new AtomicReference<>(null);
    private ThreadLocal<Node> mCurrent = ThreadLocal.withInitial(Node::new);

    public void lock() {
        Node my = mCurrent.get();
        Node prev = mTail.getAndSet(my);
        if (null == prev) {
            // 因为必须靠前一个来通知，所以如果是第一个，必须明确表明这种情况。
            // 所以tail初始化为null
            return;
        }

        prev.next = my;
        my.lock();
        while (my.locked == true) {

        }
    }

    public void unlock() {
        Node my = mCurrent.get();
        if (my.next == null) {
            if(mTail.compareAndSet(my, null)){
                // 如果确实没有后继，直接返回
                return;
            }
            // 等待next有值
            // 从lock（）知道，是先更新tail，之后再更新prev.next
            while (my.next != null) {

            }
        }
        my.next.unlock();
        my.next = null;
    }

}
