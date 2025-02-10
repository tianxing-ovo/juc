package com.ltx.entity;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
    private Runnable runnable = this::synchronizedLock;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    /**
     * 不存在死锁问题
     */
    public void reentrantLock() {
        while (true) {
            if (left.tryLock()) {
                try {
                    if (right.tryLock()) {
                        try {
                            log.debug("{} is eating", getName());
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
            // 如果未能成功获取两根筷子的锁 -> 让出CPU时间片
            Thread.yield();
        }
    }


    /**
     * 存在死锁问题
     */
    public void synchronizedLock() {
        while (true) {
            synchronized (left) {
                synchronized (right) {
                    log.info("{} is eating", getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        runnable.run();
    }
}
