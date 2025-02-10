package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author tianxing
 */
@Slf4j(topic = "Park")
public class Park {

    public static void main(String[] args) throws InterruptedException {
        unpark();
        System.out.println("------------------------");
        interruptParkThread();
    }

    /**
     * unpark
     *
     * @throws InterruptedException 中断异常
     */
    private static void unpark() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("park");
            // 阻塞
            LockSupport.park();
            log.info("resume");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("start unpark");
        LockSupport.unpark(t1);
        t1.join();
    }


    /**
     * 打断park线程
     * 打断标记为false: park会阻塞
     * 打断标记为true: park不会阻塞
     *
     * @throws InterruptedException 中断异常
     */
    public static void interruptParkThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("park");
            // 阻塞
            LockSupport.park();
            log.info("unpark");
            // true -> 重置打断标记为false
            log.info("打断标记: {}", Thread.interrupted());
            // false
            log.info("打断标记: {}", Thread.currentThread().isInterrupted());
            // 阻塞
            LockSupport.park();
            log.info("unpark");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("interrupt park thread");
        t1.interrupt();
    }
}
