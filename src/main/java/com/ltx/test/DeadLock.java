package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 死锁: 两个线程互相等待对方释放资源 -> 程序无法继续执行
 *
 * @author tianxing
 */
@Slf4j(topic = "DeadLock")
public class DeadLock {
    private static final Object A = new Object();
    private static final Object B = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (A) {
                try {
                    log.info("t1 lock A");
                    Thread.sleep(500);
                    synchronized (B) {
                        log.info("t1 lock B");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (B) {
                log.info("t2 lock B");
                synchronized (A) {
                    log.info("t2 lock A");
                }
            }
        }, "t2").start();
    }
}
