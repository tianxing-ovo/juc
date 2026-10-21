package com.ltx.liveness;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 死锁: 多个线程互相等待对方释放锁资源导致程序永久阻塞
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
                    log.info("hold lock A");
                    TimeUnit.MILLISECONDS.sleep(500);
                    synchronized (B) {
                        log.info("get lock B");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (B) {
                try {
                    log.info("hold lock B");
                    TimeUnit.MILLISECONDS.sleep(500);
                    synchronized (A) {
                        log.info("get lock A");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2").start();
    }
}
