package com.concurrent;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * A queue to execute task in order.
 *
 * @author yanbdong@cienet.com.cn
 * @since Feb 21, 2020
 */
public class SequentialExecutor {

    /**
     * Hold all the ordered tasks
     */
    private final ArrayList<Runnable> mSequentialQueue = new ArrayList<>();
    /**
     * If all the expected tasks finished.
     */
    private final CountDownLatch mTaskFinished = new CountDownLatch(1);
    /**
     * The max index of task to execute
     */
    private volatile int mDeadLine = Integer.MAX_VALUE;
    private Thread mExecutorThread;

    /**
     * Add a new task with given order.<br>
     * When the {@code order==0} is set, the tasks start to execute in order.
     */
    public void addTaskAt(Runnable task, int order) {
        if (order >= mSequentialQueue.size()) {
            synchronized (mSequentialQueue) {
                // Grow the queue with with element null
                mSequentialQueue.ensureCapacity(order + 10);
            }
        }
        mSequentialQueue.set(order, task);
        if (order == 0) {
            mExecutorThread = new Thread(() -> startExecute());
            mExecutorThread.start();
        } else {
            LockSupport.unpark(mExecutorThread);
        }
    }

    private void startExecute() {
        int nextTaskIndex = 0;
        Runnable runnable;
        do {
            while ((runnable = mSequentialQueue.get(nextTaskIndex)) == null) {
                if (Thread.currentThread().isInterrupted()) {
                    mTaskFinished.countDown();
                    return;
                }
                LockSupport.park();
            }
            try {
                runnable.run();
            } catch (Exception e) {
                // ignore
            }
        } while ((nextTaskIndex++) <= mDeadLine);
        mTaskFinished.countDown();
    }

    /**
     * Set the max execute task order.
     */
    public void setDeadLine(int deadLine) {
        mDeadLine = deadLine;
    }

    public void waitTaskDone(long timeout) {
        try {
            if (mTaskFinished.await(timeout, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
            // Ignore
        }
        // Release thread resource
        if (mExecutorThread.isAlive()) {
            return;
        }
        mExecutorThread.interrupt();
    }
}
