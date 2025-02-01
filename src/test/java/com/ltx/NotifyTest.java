package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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
public class NotifyTest {

    private final Object obj = new Object();

    @Test
    public void testNotify() throws InterruptedException {
        new Thread(() -> run("t1"), "t1").start();
        new Thread(() -> run("t2"), "t2").start();
        Thread.sleep(1000);
        synchronized (obj) {
            log.info("notify begin");
            obj.notify();
            log.info("notify end");
        }
    }

    @Test
    public void testNotifyAll() throws InterruptedException {
        new Thread(() -> run("t1"), "t1").start();
        new Thread(() -> run("t2"), "t2").start();
        Thread.sleep(1000);
        synchronized (obj) {
            log.info("notifyAll begin");
            obj.notifyAll();
            log.info("notifyAll end");
        }
    }

    private void run(String name) {
        synchronized (obj) {
            log.info("{} wait begin", name);
            try {
                obj.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("{} wait end", name);
        }
    }
}
