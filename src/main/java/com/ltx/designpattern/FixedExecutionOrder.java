package com.ltx.designpattern;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定执行顺序模式: 先打印2再打印1
 *
 * @author tianxing
 */
@Slf4j(topic = "FixedExecutionOrder")
public class FixedExecutionOrder {
    private static final Object LOCK = new Object();
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
    private static final Condition CONDITION = REENTRANT_LOCK.newCondition();
    private static boolean run = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1-synchronized 2-ReentrantLock 3-park");
        int i = scanner.nextInt();
        if (i == 1) {
            method1();
        } else if (i == 2) {
            method2();
        } else if (i == 3) {
            method3();
        }
    }

    /**
     * synchronized + wait() + notify()
     */
    public static void method1() {
        new Thread(() -> {
            synchronized (LOCK) {
                while (!run) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.info("1");
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (LOCK) {
                log.info("2");
                run = true;
                LOCK.notify();
            }
        }, "t2").start();
    }

    /**
     * ReentrantLock + Condition + lock() + unlock() + await() + signal()
     */
    public static void method2() {
        new Thread(() -> {
            REENTRANT_LOCK.lock();
            try {
                while (!run) {
                    CONDITION.await();
                }
                log.info("1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                REENTRANT_LOCK.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            REENTRANT_LOCK.lock();
            try {
                log.info("2");
                run = true;
                CONDITION.signal();
            } finally {
                REENTRANT_LOCK.unlock();
            }
        }, "t2").start();
    }

    /**
     * park() + unpark()
     */
    public static void method3() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.info("1");
        }, "t1");
        t1.start();
        new Thread(() -> {
            log.info("2");
            LockSupport.unpark(t1);
        }, "t2").start();
    }
}
