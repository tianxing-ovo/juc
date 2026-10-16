package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * notify()和notifyAll()测试
 * 1: 必须先获取锁成为owner才能调用wait()/notify()/notifyAll()
 * 2: wait()会释放锁进入WaitSet
 * wait(): 无时限等待
 * wait(long): 有时限等待 -> 超时后自动唤醒
 * notify(): 顺序唤醒一个线程(hotspot)
 * notifyAll(): 唤醒所有线程
 *
 * @author tianxing
 */
@Slf4j
class NotifyTest {

    private final Object obj = new Object();

    @Test
    @DisplayName("唤醒单个线程")
    void notifySingleThread() throws InterruptedException {
        new Thread(this::waitForNotify, "t1").start();
        new Thread(this::waitForNotify, "t2").start();
        // 等待两个线程都进入WaitSet
        Thread.sleep(1000);
        synchronized (obj) {
            log.info("唤醒单个线程");
            obj.notify();
        }
    }

    @Test
    @DisplayName("唤醒所有线程")
    void notifyAllThreads() throws InterruptedException {
        new Thread(this::waitForNotify, "t1").start();
        new Thread(this::waitForNotify, "t2").start();
        // 等待两个线程都进入WaitSet
        Thread.sleep(1000);
        synchronized (obj) {
            log.info("唤醒所有线程");
            obj.notifyAll();
        }
    }

    private void waitForNotify() {
        synchronized (obj) {
            log.info("开始等待");
            try {
                obj.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("被唤醒");
        }
    }
}
