package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Interrupt {
    public static void main(String[] args) throws InterruptedException {
        interruptBlockingThread();
    }

    /**
     * 打断阻塞线程(sleep/wait/join),会清空打断标记
     */
    public static void interruptBlockingThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("enter sleep");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // false
                log.info("打断标记: {}", Thread.currentThread().isInterrupted());
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("interrupt");
        // 打断正在睡眠的线程,sleep方法会抛出InterruptedException
        t1.interrupt();
    }
}
