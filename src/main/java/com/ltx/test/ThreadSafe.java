package com.ltx.test;

/**
 * 线程安全
 *
 * @author tianxing
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class ThreadSafe {

    private static final Object LOCK = new Object();
    private static int count;

    /**
     * 加了synchronized也会发生线程上下文切换
     */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (LOCK) {
                    count++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (LOCK) {
                    count--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("count = " + count);
    }

    /**
     * 等同于synchronized(ThreadSafe.class)
     */
    public synchronized static void decrement() {
        count--;
    }

    /**
     * 等同于synchronized(this)
     */
    public synchronized void increment() {
        count++;
    }
}
