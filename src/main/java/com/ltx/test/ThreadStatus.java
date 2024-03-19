package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 线程状态
 */
@Slf4j
public class ThreadStatus {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("enter sleep");
                // 使线程进入TIMED_WAITING状态
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        // NEW
        System.out.println(t1.getState());
        // 使线程进入RUNNABLE状态
        t1.start();
        // RUNNABLE
        System.out.println(t1.getState());
        TimeUnit.MILLISECONDS.sleep(200);
        // TIMED_WAITING
        System.out.println(t1.getState());
    }
}
