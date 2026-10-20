package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 非守护线程: 会阻止JVM退出
 *
 * @author tianxing
 */
@Slf4j(topic = "NonDaemonThread")
public class NonDaemonThread {
    public static void main(String[] args) throws InterruptedException {
        Thread nonDaemon = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    log.info("非守护线程运行中");
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "non-daemon");
        // 默认为非守护线程
        nonDaemon.start();
        TimeUnit.MILLISECONDS.sleep(1200);
        log.info("主线程运行结束");
    }
}
