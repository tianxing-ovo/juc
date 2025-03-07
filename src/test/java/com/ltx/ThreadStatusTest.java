package com.ltx;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 线程状态测试
 *
 * @author tianxing
 */
@Slf4j
public class ThreadStatusTest {

    /**
     * NEW/RUNNABLE/TIMED_WAITING/TERMINATED
     */
    @Test
    public void test1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }, "t1");
        // NEW
        System.out.println(t1.getState());
        t1.start();
        // RUNNABLE
        System.out.println(t1.getState());
        TimeUnit.MILLISECONDS.sleep(200);
        // TIMED_WAITING
        System.out.println(t1.getState());
        TimeUnit.MILLISECONDS.sleep(500);
        // TERMINATED
        System.out.println(t1.getState());
    }

    /**
     * WAITING
     */
    @Test
    public void test2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }, "t2");
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        // WAITING
        System.out.println(t2.getState());
    }

    /**
     * BLOCKED
     */
    @Test
    public void test3() throws InterruptedException {
        new Thread(() -> {
            synchronized (ThreadStatusTest.class) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }, "t1").start();
        Thread t2 = new Thread(() -> {
            synchronized (ThreadStatusTest.class) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }, "t2");
        t2.start();
        TimeUnit.MILLISECONDS.sleep(200);
        // BLOCKED
        System.out.println(t2.getState());
    }
}
