package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 活锁: 两个线程互相改变对方的结束条件 -> 最后谁也无法结束
 *
 * @author tianxing
 */
@SuppressWarnings("BusyWait")
@Slf4j(topic = "LiveLock")
public class LiveLock {
    private static int count = 10;

    public static void main(String[] args) {
        new Thread(() -> {
            while (count > 0) {
                try {
                    Thread.sleep(200);
                    count--;
                    log();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            while (count < 20) {
                try {
                    Thread.sleep(200);
                    count++;
                    log();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2").start();
    }

    public static void log() {
        log.info("count: {}", count);
    }
}
