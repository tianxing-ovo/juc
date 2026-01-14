package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用CAS实现锁
 *
 * @author tianxing
 */
@SuppressWarnings("SpellCheckingInspection")
@Slf4j(topic = "LockCas")
public class LockCas {

    // 0-未加锁 1-加锁
    private static final AtomicInteger CELLS_BUSY = new AtomicInteger(0);

    public static void main(String[] args) {
        new Thread(() -> {
            log.info("lock");
            lock();
            try {
                log.info("lock success");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            log.info("lock");
            lock();
            try {
                log.debug("lock success");
            } finally {
                unlock();
            }
        }, "t2").start();
    }

    public static void lock() {
        while (true) {
            if (CELLS_BUSY.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public static void unlock() {
        log.info("unlock");
        CELLS_BUSY.set(0);
    }
}
