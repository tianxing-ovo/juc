package com.ltx.sync;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Park测试
 *
 * @author tianxing
 */
@Slf4j(topic = "ParkTest")
class ParkTest {

    /**
     * unpark唤醒park线程
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    @DisplayName("unpark唤醒park线程")
    void unparkParkThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("进入park阻塞");
            // 阻塞
            LockSupport.park();
            log.info("恢复执行");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("唤醒t1");
        LockSupport.unpark(t1);
        t1.join();
    }

    /**
     * 打断park线程
     * 打断标记为false: park会阻塞
     * 打断标记为true: park不会阻塞
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    @DisplayName("打断park线程")
    void interruptParkThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("第一次进入park阻塞");
            // 打断标记为false(阻塞)
            LockSupport.park();
            log.info("被打断唤醒");
            // 打断标记: true
            // 重置打断标记为false
            log.info("打断标记: {}", Thread.interrupted());
            // 打断标记: false
            log.info("打断标记: {}", Thread.currentThread().isInterrupted());
            log.info("第二次进入park阻塞");
            // 打断标记为false(阻塞)
            LockSupport.park();
            log.info("被unpark唤醒");
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("打断t1");
        // 将t1的打断标记设置为true
        t1.interrupt();
        TimeUnit.SECONDS.sleep(1);
        // 当前状态: WAITING
        log.info("t1当前状态: {}", t1.getState());
        // 再次唤醒t1
        log.info("unpark唤醒t1");
        LockSupport.unpark(t1);
        // 等待t1执行完毕
        t1.join();
    }

    /**
     * 提前发放许可后park
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    @DisplayName("提前发放许可后park")
    void unparkBeforePark() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                // 确保主线程先执行unpark
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("准备进入park");
            // 拥有许可(不阻塞)
            LockSupport.park();
            log.info("未被阻塞直接通过");
        }, "t1");
        t1.start();
        log.info("提前发放许可");
        // 提前发放许可(permit上限为1)
        LockSupport.unpark(t1);
        t1.join();
    }
}