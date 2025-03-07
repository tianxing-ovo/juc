package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 打断测试
 *
 * @author tianxing
 */
@Slf4j
public class InterruptTest {

    /**
     * 打断阻塞线程(sleep/wait/join)会清除打断标记(重置为false)
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void interruptBlockingThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("enter sleep");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // false
                log.info("打断标记: {}", Thread.currentThread().isInterrupted());
                // 重新设置打断标记
                Thread.currentThread().interrupt();
                // true
                log.info("打断标记: {}", Thread.currentThread().isInterrupted());
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("interrupt blocking thread");
        // 打断正在睡眠的线程 -> sleep方法会抛出InterruptedException
        t1.interrupt();
        t1.join();
    }

    /**
     * 打断正常运行的线程不会清除打断标记
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void interruptNormalThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("被打断了,退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("interrupt normal thread");
        // 打断正常运行的线程 -> 打断标记变为true
        t1.interrupt();
        t1.join();
    }
}
