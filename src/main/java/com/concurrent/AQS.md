* state 表示资源的状态
* 队列 表示等待资源的队列

> 何为快速入队？直接把我们刚才构造的Node的前驱指针指向当前尾节点，然后通过CAS操作把我们刚才构造的node作为新的尾节点，最后再把原来老的尾节点的后继指针指向现在的新的尾节点。
> 其实队列的初始化在同步器的整个生命周期中只会执行一次，后续的入队操作都会按快速入队的方式入队。

整个实现中包含两种等待state方式，自旋和park（线程调度）。
自旋通过for(;;)，park通过LockSupport。般来说，如果一个node通过自旋无望获取state的时候，会进入park，而将前一个节点置为signal，
表示如果兄弟你去获得了state，执行了的话，告诉我，让我提前unpack，回到自旋等待的状态，好快速相应。

### 独占式
#### 获取
如果一个线程想要获取资源，主要是看state给不给，即java.util.concurrent.locks.AbstractQueuedSynchronizer.tryAcquire。
所以state的管理主要是靠java.util.concurrent.locks.AbstractQueuedSynchronizer.tryAcquire。

只要tryAcquire失败一次，马上送入队列中去java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued。

队列都是尾入式，所以只要在入队的时候保证了有序性。之后在队列中查询的操作，都是可并行的。

acquireQueued方法会一直检查更新节点的状态值，直到当前节点的前驱节点状态值为SIGNAL，这是AQS约定的，只有前继节点的waitStatus是SIGNAL，当前节点才可以安心的去阻塞。

在队列中的节点只有当其是head之后的节点的情况下，才会去尝试获取state。
如果获得了state，这个时候该节点就成为了队列的head，之后调用则java.util.concurrent.locks.AbstractQueuedSynchronizer.cancelAcquire。
将head置为cancelled。这样后续节点在入队之后，通过acquireQueued，就会跳过head。

![](/Users/mats/Documents/workspace/JavaTest/res/image/20180528190734895.png)

