package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Join {
    static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                i = 10;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        t1.start();
        // 等待t1线程运行结束
        t1.join();
        // 等待t1线程运行结束,最多等待1000ms
        t1.join(1000);
        log.info("{}", i);
    }
}
