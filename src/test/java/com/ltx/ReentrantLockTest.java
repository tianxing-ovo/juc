package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock测试
 * lock(): 获取锁
 * lockInterruptibly(): 获取锁(可中断)
 * tryLock(): 尝试获取锁
 * unlock(): 释放锁
 * await(): 无时限等待
 * signal(): 顺序唤醒一个线程(hotspot)
 * signalAll(): 唤醒所有线程
 *
 * @author tianxing
 */
@Slf4j(topic = "ReentrantLockTest")
public class ReentrantLockTest {
    // 默认是非公平锁
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition smokeCondition = reentrantLock.newCondition();
    private final Condition takeoutCondition = reentrantLock.newCondition();
    private boolean hasSmoke = false;
    private boolean hasTakeout = false;

    /**
     * 可重入: 同一个线程可以多次获取锁
     */
    @Test
    public void reentrant() {
        outerMethod();
    }

    /**
     * 可中断: 在等待锁的过程中可以中断
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void interruptibly() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("t1获取锁");
                reentrantLock.lockInterruptibly();
                log.info("t1获取到锁");
            } catch (InterruptedException e) {
                log.info("t1被中断");
            } finally {
                if (reentrantLock.isHeldByCurrentThread()) {
                    reentrantLock.unlock();
                    log.info("t1释放锁");
                }
            }
        }, "t1");
        reentrantLock.lock();
        t1.start();
        Thread.sleep(1000);
        log.info("打断t1");
        t1.interrupt();
        t1.join();
    }

    /**
     * 锁超时: 在等待锁的过程中可以超时
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void tryLock() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("t1尝试获取锁");
                if (!reentrantLock.tryLock(1, TimeUnit.SECONDS)) {
                    log.info("t1获取锁失败");
                    return;
                }
                log.debug("t1获取到锁");
            } catch (InterruptedException e) {
                log.debug("t1被中断");
            } finally {
                if (reentrantLock.isHeldByCurrentThread()) {
                    reentrantLock.unlock();
                }
            }
        }, "t1");
        reentrantLock.lock();
        t1.start();
        t1.join();
    }

    /**
     * 条件变量: WaitSet(等待队列)
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void condition() throws InterruptedException {
        new Thread(() -> {
            reentrantLock.lock();
            try {
                while (!hasSmoke) {
                    log.info("t1等待烟");
                    smokeCondition.await();
                }
                log.info("t1开始吸烟");
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
                    log.info("t2等待外卖");
                    takeoutCondition.await();
                }
                log.info("t2开始吃外卖");
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
                log.info("t3生产烟");
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
                log.info("t4生产外卖");
                hasTakeout = true;
                takeoutCondition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, "t4");
        t4.start();
        t4.join();
    }

    public void outerMethod() {
        reentrantLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " - Entered outerMethod");
            innerMethod();
        } finally {
            System.out.println(Thread.currentThread().getName() + " - Exiting outerMethod");
            reentrantLock.unlock();
        }
    }

    public void innerMethod() {
        reentrantLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " - Entered innerMethod");
        } finally {
            System.out.println(Thread.currentThread().getName() + " - Exiting innerMethod");
            reentrantLock.unlock();
        }
    }
}
