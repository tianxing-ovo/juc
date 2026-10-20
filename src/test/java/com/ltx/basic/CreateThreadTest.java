package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 创建线程测试
 *
 * @author tianxing
 */
@Slf4j(topic = "CreateThreadTest")
class CreateThreadTest {

    @Test
    @DisplayName("继承Thread类创建线程")
    void createByThread() throws InterruptedException {
        Thread thread = new Thread("thread-worker") {
            @Override
            public void run() {
                log.info("继承Thread类");
            }
        };
        thread.start();
        thread.join();
    }

    @Test
    @DisplayName("实现Runnable接口创建线程")
    void createByRunnable() throws InterruptedException {
        Thread thread = new Thread(() -> log.info("实现Runnable接口"), "runnable-worker");
        thread.start();
        thread.join();
    }

    @Test
    @DisplayName("实现Callable接口创建线程")
    void createByCallable() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            log.info("实现Callable接口");
            TimeUnit.MILLISECONDS.sleep(200);
            return 1;
        });
        Thread thread = new Thread(futureTask, "callable-worker");
        thread.start();
        log.info("等待Callable接口执行完成");
        // 阻塞获取结果
        Integer result = futureTask.get();
        log.info("result: {}", result);
        assertEquals(1, result);
    }
}
