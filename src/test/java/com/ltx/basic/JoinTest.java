package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * join测试
 *
 * @author tianxing
 */
@Slf4j(topic = "JoinTest")
class JoinTest {

    @Test
    @DisplayName("无限等待")
    void joinUntilFinished() throws InterruptedException {
        int[] result = new int[1];
        Thread thread = new Thread(() -> {
            try {
                log.info("开始休眠100毫秒");
                TimeUnit.MILLISECONDS.sleep(100);
                result[0] = 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "worker");
        thread.start();
        // 阻塞等待worker线程运行结束
        thread.join();
        log.info("无限等待结果: {}", result[0]);
        assertEquals(10, result[0]);
    }

    @Test
    @DisplayName("超时等待")
    void joinWithTimeout() throws InterruptedException {
        int[] result = new int[1];
        Thread thread = new Thread(() -> {
            try {
                log.info("开始休眠300毫秒");
                TimeUnit.MILLISECONDS.sleep(300);
                result[0] = 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "worker");
        thread.start();
        // 阻塞等待worker线程运行结束(最多等待50ms)
        thread.join(50);
        log.info("超时等待结果: {}", result[0]);
        assertEquals(0, result[0]);
    }
}
