package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 打断测试
 *
 * @author tianxing
 */
@Slf4j
class InterruptTest {

    /**
     * 打断阻塞线程(sleep/wait/join)会清除打断标记(重置为false)
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    @DisplayName("打断阻塞线程")
    void interruptBlockingThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("t1进入休眠");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // 打断标记: false
                log.info("打断标记: {}", Thread.currentThread().isInterrupted());
                assertFalse(Thread.currentThread().isInterrupted());
                // 手动恢复打断标记(防止吞掉打断信号)
                Thread.currentThread().interrupt();
                // 手动恢复后打断标记: true
                log.info("手动恢复后打断标记: {}", Thread.currentThread().isInterrupted());
                assertTrue(Thread.currentThread().isInterrupted());
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("主线程打断正在休眠的t1");
        // 打断正在睡眠的线程(sleep方法会抛出InterruptedException)
        t1.interrupt();
        t1.join();
    }

    /**
     * 打断正常运行的线程不会清除打断标记
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    @DisplayName("打断正常运行线程")
    void interruptNormalThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("t1检测到打断标记并退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("主线程打断正在运行的t1");
        // 打断正常运行的线程(打断标记变为true)
        t1.interrupt();
        t1.join();
        // 正常打断后标记: true
        log.info("正常打断后标记: {}", t1.isInterrupted());
        assertTrue(t1.isInterrupted());
    }
}
