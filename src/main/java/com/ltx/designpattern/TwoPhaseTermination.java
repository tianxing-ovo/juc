package com.ltx.designpattern;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式
 *
 * @author tianxing
 */
@Slf4j(topic = "TwoPhaseTermination")
public class TwoPhaseTermination {
    private static Thread monitor;

    public static void start() {
        monitor = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("料理后事");
                    break;
                }
                try {
                    // 情况1: 睡眠中被打断
                    TimeUnit.SECONDS.sleep(1);
                    // 情况2: 运行中被打断
                    log.info("执行监控记录");
                } catch (InterruptedException e) {
                    // 重新设置打断标记
                    Thread.currentThread().interrupt();
                }
            }
        }, "monitor");
        monitor.start();
    }

    public static void stop() {
        monitor.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination.start();
        TimeUnit.MILLISECONDS.sleep(3500);
        TwoPhaseTermination.stop();
    }
}
