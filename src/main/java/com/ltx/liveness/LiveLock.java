package com.ltx.liveness;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 活锁: 多个线程互相改变对方的结束条件导致任务始终无法结束
 *
 * @author tianxing
 */
@Slf4j(topic = "LiveLock")
public class LiveLock {

    private static final AtomicInteger count = new AtomicInteger(10);

    public static void main(String[] args) {
        new Thread(() -> {
            while (count.get() > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    count.decrementAndGet();
                    log();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
        new Thread(() -> {
            while (count.get() < 20) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    count.incrementAndGet();
                    log();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2").start();
    }

    public static void log() {
        log.info("count: {}", count.get());
    }
}
