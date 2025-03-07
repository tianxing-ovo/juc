package com.ltx.test;


import lombok.extern.slf4j.Slf4j;

/**
 * 守护线程: 不会阻止JVM退出
 *
 * @author tianxing
 */
@Slf4j(topic = "DaemonThread")
public class DaemonThread {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.info("t1 is over");
        }, "t1");
        // 设置为守护线程
        t1.setDaemon(true);
        t1.start();
        log.info("main is over");
    }
}
