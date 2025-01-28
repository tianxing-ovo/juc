package com.ltx.test;

/**
 * 线程优先级
 *
 * @author tianxing
 */
public class ThreadPriority {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            int count = 0;
            while (count < 1000) {
                System.out.println("t1: " + count++);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            int count = 0;
            while (count < 1000) {
                // 当前正在执行的线程放弃CPU执行时间片 -> 回到就绪状态 -> 将执行机会让给优先级相同或更高且已准备好运行的线程
                Thread.yield();
                System.out.println("t2: " + count++);
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
