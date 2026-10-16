package com.ltx.designpattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定执行顺序模式测试(先打印2再打印1)
 *
 * @author tianxing
 */
@Slf4j
class FixedExecutionOrderTest {

    private final Object lock = new Object();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition condition = reentrantLock.newCondition();
    private boolean run = false;
    private Thread t1;
    private Thread t2;

    /**
     * synchronized + wait() + notify()
     */
    @Test
    @DisplayName("使用synchronized实现固定执行顺序模式")
    void fixedOrderBySynchronized() throws InterruptedException {
        t1 = new Thread(() -> {
            synchronized (lock) {
                while (!run) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("1");
            }
        }, "t1");
        t2 = new Thread(() -> {
            synchronized (lock) {
                log.info("2");
                run = true;
                lock.notify();
            }
        }, "t2");
        // 启动线程
        t1.start();
        t2.start();
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
    }

    /**
     * ReentrantLock + Condition + lock() + unlock() + await() + signal()
     */
    @Test
    @DisplayName("使用ReentrantLock实现固定执行顺序模式")
    void fixedOrderByReentrantLock() throws InterruptedException {
        t1 = new Thread(() -> {
            reentrantLock.lock();
            try {
                while (!run) {
                    condition.await();
                }
                log.info("1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                reentrantLock.unlock();
            }
        }, "t1");
        t2 = new Thread(() -> {
            reentrantLock.lock();
            try {
                log.info("2");
                run = true;
                condition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, "t2");
        // 启动线程
        t1.start();
        t2.start();
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
    }

    /**
     * LockSupport + park() + unpark()
     */
    @Test
    @DisplayName("使用LockSupport实现固定执行顺序模式")
    void fixedOrderByLockSupport() throws InterruptedException {
        t1 = new Thread(() -> {
            LockSupport.park();
            log.info("1");
        }, "t1");
        t2 = new Thread(() -> {
            log.info("2");
            LockSupport.unpark(t1);
        }, "t2");
        // 启动线程
        t1.start();
        t2.start();
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
    }
}
