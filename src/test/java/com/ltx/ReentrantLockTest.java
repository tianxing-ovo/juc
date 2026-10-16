package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock测试
 * lock(): 获取锁
 * lockInterruptibly(): 获取锁(可中断)
 * tryLock(long timeout, TimeUnit unit): 尝试获取锁(可中断可超时)
 * unlock(): 释放锁
 * await(): 无时限等待
 * signal(): 顺序唤醒一个线程(hotspot)
 * signalAll(): 唤醒所有线程
 *
 * @author tianxing
 */
@Slf4j(topic = "ReentrantLockTest")
class ReentrantLockTest {
    // 默认是非公平锁
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition smokeCondition = reentrantLock.newCondition();
    private final Condition takeoutCondition = reentrantLock.newCondition();
    private boolean hasSmoke = false;
    private boolean hasTakeout = false;


    @Test
    @DisplayName("可重入特性")
    void reentrant() {
        reentrantLock.lock();
        try {
            log.info("外层获取锁");
            // 进入内层方法再次获取同一把锁
            inner();
        } finally {
            log.info("外层完全释放锁");
            reentrantLock.unlock();
        }
    }

    @Test
    @DisplayName("可中断特性")
    void interruptibly() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("可中断地获取锁");
                reentrantLock.lockInterruptibly();
                log.info("获取到锁");
            } catch (InterruptedException e) {
                log.info("等待锁期间被外部打断");
            } finally {
                if (reentrantLock.isHeldByCurrentThread()) {
                    reentrantLock.unlock();
                    log.info("释放可中断获取的锁");
                }
            }
        }, "t1");
        // 主线程先获取锁使t1陷入阻塞等待
        reentrantLock.lock();
        try {
            // 启动t1
            t1.start();
            // 确保t1已进入阻塞等待
            Thread.sleep(1000);
            log.info("打断t1");
            t1.interrupt();
            t1.join();
        } finally {
            reentrantLock.unlock();
        }
    }

    @Test
    @DisplayName("锁超时特性")
    void tryLock() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("尝试限时获取锁");
                if (!reentrantLock.tryLock(1, TimeUnit.SECONDS)) {
                    log.info("获取锁超时放弃");
                    return;
                }
                log.info("限时获取到锁");
            } catch (InterruptedException e) {
                log.info("限时等待中被中断");
            } finally {
                if (reentrantLock.isHeldByCurrentThread()) {
                    reentrantLock.unlock();
                    log.info("释放限时获取的锁");
                }
            }
        }, "t1");
        reentrantLock.lock();
        try {
            t1.start();
            t1.join();
        } finally {
            reentrantLock.unlock();
        }
    }

    @Test
    @DisplayName("多条件变量特性")
    void multiCondition() throws InterruptedException {
        new Thread(() -> {
            reentrantLock.lock();
            try {
                while (!hasSmoke) {
                    log.info("等待烟");
                    smokeCondition.await();
                }
                log.info("开始吸烟");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                reentrantLock.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            reentrantLock.lock();
            try {
                while (!hasTakeout) {
                    log.info("等待外卖");
                    takeoutCondition.await();
                }
                log.info("开始吃外卖");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                reentrantLock.unlock();
            }
        }, "t2").start();
        Thread.sleep(1000);
        new Thread(() -> {
            reentrantLock.lock();
            try {
                log.info("生产烟");
                hasSmoke = true;
                smokeCondition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, "t3").start();
        Thread.sleep(1000);
        Thread t4 = new Thread(() -> {
            reentrantLock.lock();
            try {
                log.info("生产外卖");
                hasTakeout = true;
                takeoutCondition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, "t4");
        t4.start();
        t4.join();
    }

    private void inner() {
        reentrantLock.lock();
        try {
            log.info("内层重入获取锁");
        } finally {
            log.info("内层释放锁");
            reentrantLock.unlock();
        }
    }
}
