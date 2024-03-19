package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "Park")
public class Park {

    /**
     * 中断标记为false: park会阻塞
     * 中断标记为true: park不会阻塞
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("park");
            // 阻塞
            LockSupport.park();
            log.info("unpark");
            // true,重置打断标记为false
            log.info("打断标记: {}", Thread.interrupted());
            LockSupport.park();
            log.info("unpark");
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("interrupt");
        t1.interrupt();
    }
}
