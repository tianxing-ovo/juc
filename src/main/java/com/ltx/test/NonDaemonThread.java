package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 非守护线程: 会阻止JVM退出
 *
 * @author tianxing
 */
@Slf4j(topic = "NonDaemonThread")
public class NonDaemonThread {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            log.info("t1 is over");
        }, "t1").start();
        log.info("main is over");
    }
}
