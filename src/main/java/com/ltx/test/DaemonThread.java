package com.ltx.test;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程
 */
@Slf4j
public class DaemonThread {

    /**
     * 所有非守护线程运行结束,Java进程才会结束,守护线程会强制退出
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                log.info("守护线程运行结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        t1.setDaemon(true);
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("main线程运行结束");
    }
}
