package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程: 不会阻止JVM退出
 *
 * @author tianxing
 */
@Slf4j(topic = "DaemonThread")
public class DaemonThread {
    public static void main(String[] args) throws InterruptedException {
        Thread daemon = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    log.info("守护线程运行中");
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "daemon");
        // 设置为守护线程
        daemon.setDaemon(true);
        daemon.start();
        TimeUnit.MILLISECONDS.sleep(1200);
        log.info("主线程运行结束");
    }
}
