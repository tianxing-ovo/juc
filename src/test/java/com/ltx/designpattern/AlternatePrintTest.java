package com.ltx.designpattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 交替打印测试(123123123123123)
 *
 * @author tianxing
 */
class AlternatePrintTest {

    private final Object lock = new Object();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition condition1 = reentrantLock.newCondition();
    private final Condition condition2 = reentrantLock.newCondition();
    private final Condition condition3 = reentrantLock.newCondition();
    private int step = 1;
    private Thread t1;
    private Thread t2;
    private Thread t3;

    /**
     * synchronized + wait() + notifyAll()
     */
    @Test
    @DisplayName("使用synchronized实现交替打印")
    void alternatePrintBySynchronized() throws InterruptedException {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (step != 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(1);
                    step = 2;
                    lock.notifyAll();
                }
            }
        }, "t1");
        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (step != 2) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(2);
                    step = 3;
                    lock.notifyAll();
                }
            }
        }, "t2");
        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (lock) {
                    while (step != 3) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(3);
                    step = 1;
                    lock.notifyAll();
                }
            }
        }, "t3");
        // 启动线程
        t1.start();
        t2.start();
        t3.start();
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
        t3.join();
        System.out.println();
    }

    /**
     * ReentrantLock + Condition + lock() + unlock() + await() + signal()
     */
    @Test
    @DisplayName("使用ReentrantLock实现交替打印")
    void alternatePrintByReentrantLock() throws InterruptedException {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                reentrantLock.lock();
                try {
                    while (step != 1) {
                        condition1.await();
                    }
                    System.out.print(1);
                    step = 2;
                    condition2.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "t1");
        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                reentrantLock.lock();
                try {
                    while (step != 2) {
                        condition2.await();
                    }
                    System.out.print(2);
                    step = 3;
                    condition3.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "t2");
        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                reentrantLock.lock();
                try {
                    while (step != 3) {
                        condition3.await();
                    }
                    System.out.print(3);
                    step = 1;
                    condition1.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "t3");
        // 启动线程
        t1.start();
        t2.start();
        t3.start();
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
        t3.join();
        System.out.println();
    }

    /**
     * LockSupport + park() + unpark()
     */
    @Test
    @DisplayName("使用LockSupport实现交替打印")
    void alternatePrintByLockSupport() throws InterruptedException {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(1);
                LockSupport.unpark(t2);
            }
        }, "t1");
        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(2);
                LockSupport.unpark(t3);
            }
        }, "t2");
        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(3);
                LockSupport.unpark(t1);
            }
        }, "t3");
        // 启动线程
        t1.start();
        t2.start();
        t3.start();
        // 给t1发放许可证
        LockSupport.unpark(t1);
        // 等待所有线程执行完毕
        t1.join();
        t2.join();
        t3.join();
        System.out.println();
    }
}
