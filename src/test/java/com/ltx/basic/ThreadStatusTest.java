package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 线程状态测试
 *
 * @author tianxing
 */
@Slf4j
class ThreadStatusTest {

    @Test
    @DisplayName("生命周期与超时等待")
    void lifecycleAndTimedWaiting() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "t1");
        // 创建后状态: NEW
        Thread.State state = t1.getState();
        log.info("创建后状态: {}", state);
        assertEquals(Thread.State.NEW, state);
        t1.start();
        // 启动后状态: RUNNABLE
        state = t1.getState();
        log.info("启动后状态: {}", state);
        assertEquals(Thread.State.RUNNABLE, state);
        TimeUnit.MILLISECONDS.sleep(200);
        // 休眠中状态: TIMED_WAITING
        state = t1.getState();
        log.info("休眠中状态: {}", state);
        assertEquals(Thread.State.TIMED_WAITING, state);
        t1.join();
        // 终止后状态: TERMINATED
        state = t1.getState();
        log.info("终止后状态: {}", state);
        assertEquals(Thread.State.TERMINATED, state);
    }

    @Test
    @DisplayName("等待状态")
    void waitingState() throws InterruptedException {
        // 获取主线程
        Thread mainThread = Thread.currentThread();
        Thread t1 = new Thread(() -> {
            try {
                // t1等待主线程
                mainThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(100);
        // join等待状态: WAITING
        Thread.State state = t1.getState();
        log.info("join等待状态: {}", state);
        assertEquals(Thread.State.WAITING, state);
        // 中断t1以避免后台悬挂
        t1.interrupt();
    }

    @Test
    @DisplayName("阻塞状态")
    void blockedState() throws InterruptedException {
        new Thread(() -> {
            synchronized (ThreadStatusTest.class) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "t1").start();
        Thread t2 = new Thread(() -> {
            synchronized (ThreadStatusTest.class) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "t2");
        t2.start();
        TimeUnit.MILLISECONDS.sleep(200);
        // 锁竞争阻塞状态: BLOCKED
        Thread.State state = t2.getState();
        log.info("锁竞争阻塞状态: {}", state);
        assertEquals(Thread.State.BLOCKED, state);
    }
}
