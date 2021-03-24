package com.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 这个锁是线程间都可见的。因此{@linkplain #mTail}是唯一这个锁的共享属性。而{@linkplain #mPrev} and {@linkplain #mCurrent}是
 * 各个线程私有的，存储在ThreadLocal中，这就是隐式链条。
 * 作为一个锁，只要工作起来，就一直存在一条隐式的链条。新的线程如果申请获取锁，就自觉抢夺{@linkplain #mTail}入队，然后等待
 * 之前的所有节点都不用了，再轮到自己。
 *
 * @author yanbdong@cienet.com.cn
 * @since Feb 20, 2020
 */
class CLH {

    static class Node {

        /**
         * 锁状态：默认为false，表示线程没有获取到锁；true表示线程获取到锁或正在等待
         */
        volatile boolean locked = false;
    }

    private AtomicReference<Node> mTail = new AtomicReference<>(new Node());
    private ThreadLocal<Node> mPrev = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<Node> mCurrent = ThreadLocal.withInitial(Node::new);

    /**
     * 当前线程希望进入临界区。因此展开了和其他线程的争夺。
     * 自旋有两个地方，一个和其他并发的线程一起对尾节点的争抢；一个是成功占坑后，等待之前节点释放资源
     */
    public void lock() {
        // 取出当前线程的节点
        Node my = mCurrent.get();
        // 开始自觉排队，将自己插入到队尾，以此维护了一个等待资源的队列。
        // 这就是和其他线程争抢的资源的过程，可见是公平的
        Node prev = mTail.getAndSet(my);
        // 记录上一个
        mPrev.set(prev);
        // 表明我要开始独占资源了，但前提是，位于我前面的节点释放了资源
        my.locked = true;
        // 于是自旋等待前面的节点释放资源
        while (prev.locked == true) {
            // 可见，本线程能工作的前提，去要依赖前一个节点的信息。在NUMA下，获取前一个节点的信息很耗时。
        }
    }

    /**
     * 当前线程可以推出临界区了。
     */
    public void unlock() {
        Node my = mCurrent.get();
        my.locked = false;
        // 为了防止该线程打算尝试重复获取锁。因为重复获取的情况下，mTail里面存的恰好是自己，这会造成当前节点和
        // prev节点都是自己的情况。这种情况下，怎么可能在设置locked=true下，又等待prev节点（其实还是自己）又
        // 变为false。
        // 所以不方便更改mTail，就改变自己了。
        mCurrent.set(mPrev.get());
    }

}
