package com.ltx.designpattern;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 交替打印: 123123123123123
 *
 * @author tianxing
 */
public class AlternatePrint {

    private static final Object LOCK = new Object();
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
    private static final Condition CONDITION1 = REENTRANT_LOCK.newCondition();
    private static final Condition CONDITION2 = REENTRANT_LOCK.newCondition();
    private static final Condition CONDITION3 = REENTRANT_LOCK.newCondition();
    private static Thread t1;
    private static Thread t2;
    private static Thread t3;
    private static int step = 1;

    public static void main(String[] args) throws InterruptedException {
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
            for (int i = 0; i < 5; i++) {
                synchronized (LOCK) {
                    while (step != 1) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(1);
                    step = 2;
                    LOCK.notifyAll();
                }
            }
        }, "t1").start();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (LOCK) {
                    while (step != 2) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(2);
                    step = 3;
                    LOCK.notifyAll();
                }
            }
        }, "t2").start();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (LOCK) {
                    while (step != 3) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.print(3);
                    step = 1;
                    LOCK.notifyAll();
                }
            }
        }, "t3").start();
    }

    /**
     * ReentrantLock + Condition + lock() + unlock() + await() + signal()
     */
    public static void method2() throws InterruptedException {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                REENTRANT_LOCK.lock();
                try {
                    CONDITION1.await();
                    System.out.print(1);
                    CONDITION2.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    REENTRANT_LOCK.unlock();
                }
            }
        }, "t1").start();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                REENTRANT_LOCK.lock();
                try {
                    CONDITION2.await();
                    System.out.print(2);
                    CONDITION3.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    REENTRANT_LOCK.unlock();
                }
            }
        }, "t2").start();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                REENTRANT_LOCK.lock();
                try {
                    CONDITION3.await();
                    System.out.print(3);
                    CONDITION1.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    REENTRANT_LOCK.unlock();
                }
            }
        }, "t3").start();
        Thread.sleep(100);
        REENTRANT_LOCK.lock();
        try {
            CONDITION1.signal();
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }

    /**
     * park() + unpark()
     */
    public static void method3() throws InterruptedException {
        t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(1);
                LockSupport.unpark(t2);
            }
        }, "t1");
        t1.start();
        t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(2);
                LockSupport.unpark(t3);
            }
        }, "t2");
        t2.start();
        t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                LockSupport.park();
                System.out.print(3);
                LockSupport.unpark(t1);
            }
        }, "t3");
        t3.start();
        Thread.sleep(100);
        LockSupport.unpark(t1);
    }
}
