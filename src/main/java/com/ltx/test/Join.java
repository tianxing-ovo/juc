package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author tianxing
 */
@Slf4j(topic = "Join")
public class Join {
    private static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("enter sleep");
                TimeUnit.SECONDS.sleep(1);
                i = 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t1");
        t1.start();
        // 阻塞等待t1线程运行结束
        t1.join();
        // 阻塞等待t1线程运行结束(最多等待1000ms)
        t1.join(1000);
        log.info("i = {}", i);
    }
}
