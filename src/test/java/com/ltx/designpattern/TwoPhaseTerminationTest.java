package com.ltx.designpattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 两阶段终止模式测试
 *
 * @author tianxing
 */
@Slf4j
class TwoPhaseTerminationTest {

    private volatile Thread monitor;
    private volatile boolean stop = false;
    private final AtomicBoolean starting = new AtomicBoolean(false);

    @Test
    @DisplayName("使用interrupt实现两阶段终止模式")
    void terminateByInterrupt() throws InterruptedException {
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
                    doMonitor();
                } catch (InterruptedException e) {
                    // 重新设置打断标记
                    Thread.currentThread().interrupt();
                }
            }
        }, "monitor");
        monitor.start();
        // 主线程休眠3.5秒让监控线程充分运行
        TimeUnit.MILLISECONDS.sleep(3500);
        // 发送终止打断信号通知线程退出
        monitor.interrupt();
        // 等待监控线程料理后事并优雅退出
        monitor.join();
    }

    @Test
    @DisplayName("使用volatile实现两阶段终止模式")
    void terminateByVolatile() throws InterruptedException {
        // 启动监控线程
        startMonitor();
        // 主线程休眠3.5秒让监控线程充分运行
        TimeUnit.MILLISECONDS.sleep(3500);
        // 停止监控线程
        stopMonitor();
        // 等待监控线程料理后事并优雅退出
        monitor.join();
    }

    private void startMonitor() {
        // Balking(犹豫模式)CAS无锁防止重复启动
        if (!starting.compareAndSet(false, true)) {
            return;
        }
        monitor = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.info("料理后事");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                    doMonitor();
                } catch (InterruptedException ignored) {
                }
            }
        }, "monitor");
        monitor.start();
    }

    private void stopMonitor() {
        stop = true;
        // 立即进入下一次循环
        monitor.interrupt();
    }

    private void doMonitor() {
        log.info("执行监控记录");
    }
}
