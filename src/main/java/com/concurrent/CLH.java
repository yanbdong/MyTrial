package com.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yanbdong@cienet.com.cn
 * @since Feb 20, 2020
 */
class CLH {

    static class Node {

        volatile boolean locked;

        Node unlock() {
            locked = false;
            return this;
        }

        Node lock() {
            locked = true;
            return this;
        }
    }

    private AtomicReference<Node> mTail = new AtomicReference<>(new Node().unlock());
    private ThreadLocal<Node> mPrev = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<Node> mCurrent = ThreadLocal.withInitial(Node::new);

    public void lock() {
        Node my = mCurrent.get();
        Node prev = mTail.getAndSet(my);
        mPrev.set(prev);
        my.lock();
        while (prev.locked == true) {

        }
    }

    public void unlock() {
        Node my = mCurrent.get();
        my.unlock();
        // 之前一阶段的令牌已经生效，必须长期置为false，以方便后续节点触发
        // 所有在后续节点触发之前，必须保持false状态
        // 而如果此时该线程打算尝试重复获取锁，那么它应该去生成一个新的令牌，而不是重用之前那个令牌
        mCurrent.set(mPrev.get());
    }

}
