package com.ltx.entity;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 哲学家
 *
 * @author tianxing
 */
@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
@Slf4j(topic = "Philosopher")
@Setter
public class Philosopher extends Thread {

    private final Chopstick left;
    private final Chopstick right;
    /**
     * 是否使用ReentrantLock
     */
    private boolean useReentrantLock;
    /**
     * 就餐次数计数器
     */
    private final AtomicInteger eatCount = new AtomicInteger();

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    /**
     * 获取累计就餐次数
     *
     * @return 就餐次数
     */
    public int getEatCount() {
        return eatCount.get();
    }

    /**
     * 使用ReentrantLock锁(不存在死锁问题)
     */
    public void reentrantLock() {
        while (true) {
            if (left.tryLock()) {
                try {
                    if (right.tryLock()) {
                        try {
                            eat();
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
            // 让出CPU时间片防止自旋空转并提高公平性
            Thread.yield();
        }
    }


    /**
     * 使用synchronized锁(存在死锁问题)
     */
    public void synchronizedLock() {
        while (true) {
            synchronized (left) {
                try {
                    // 拿到左筷子后短暂停顿以确保所有人持有左筷子稳定复现死锁
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (right) {
                    eat();
                }
            }
        }
    }

    /**
     * 哲学家就餐
     */
    private void eat() {
        log.info("使用 [{}] 和 [{}] 正在就餐", left.getName(), right.getName());
        eatCount.incrementAndGet();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (useReentrantLock) {
            reentrantLock();
        } else {
            synchronizedLock();
        }
    }
}
