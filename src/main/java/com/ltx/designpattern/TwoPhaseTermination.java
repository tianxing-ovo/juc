package com.ltx.designpattern;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式
 *
 * @author tianxing
 */
@Slf4j(topic = "TwoPhaseTermination")
public class TwoPhaseTermination {
    private static Thread monitor;
    private static volatile boolean stop = false;
    // 是否执行过start方法 -> start方法只需执行一次
    private static boolean starting = false;

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1-interrupt 2-volatile");
        int i = scanner.nextInt();
        if (i == 1) {
            TwoPhaseTermination.start();
            TimeUnit.MILLISECONDS.sleep(3500);
            TwoPhaseTermination.stop();
        } else if (i == 2) {
            TwoPhaseTermination.startV1();
            TimeUnit.MILLISECONDS.sleep(3500);
            TwoPhaseTermination.stopV1();
        }
    }

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
                    info();
                } catch (InterruptedException e) {
                    // 重新设置打断标记
                    Thread.currentThread().interrupt();
                }
            }
        }, "monitor");
        monitor.start();
    }

    public static void startV1() {
        synchronized (TwoPhaseTermination.class) {
            // Balking(犹豫)模式: 防止重复启动
            if (starting) {
                return;
            }
            starting = true;
        }
        monitor = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.info("料理后事");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                    info();
                } catch (InterruptedException ignored) {
                }
            }
        }, "monitor");
        monitor.start();
    }

    public static void stop() {
        monitor.interrupt();
    }

    public static void stopV1() {
        stop = true;
        // 立即进入下一次循环
        monitor.interrupt();
    }

    public static void info() {
        log.info("执行监控记录");
    }
}
