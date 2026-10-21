package com.ltx.basic;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程优先级
 *
 * @author tianxing
 */
@Slf4j(topic = "ThreadPriority")
public class ThreadPriority {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            int count = 0;
            while (count < 1000) {
                log.info("{}", count++);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            int count = 0;
            while (count < 1000) {
                // 提示让出CPU时间片(由运行态退回就绪态且不释放锁)
                Thread.yield();
                log.info("{}", count++);
            }
        }, "t2");
        // 设置最高优先级
        t1.setPriority(Thread.MAX_PRIORITY);
        // 设置最低优先级
        t2.setPriority(Thread.MIN_PRIORITY);
        t1.start();
        t2.start();
    }
}
