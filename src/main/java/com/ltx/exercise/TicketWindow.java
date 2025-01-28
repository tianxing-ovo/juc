package com.ltx.exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 售票窗口
 *
 * @author tianxing
 */
public class TicketWindow {

    /**
     * 余票数
     */
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public static void main(String[] args) throws InterruptedException {
        TicketWindow window = new TicketWindow(1000);
        // 所有线程的集合
        List<Thread> threadList = new ArrayList<>();
        // 卖出总票数
        AtomicInteger sum = new AtomicInteger();
        for (int i = 0; i < 200; i++) {
            Thread thread = new Thread(() -> sum.addAndGet(window.sell(randomAmount())));
            threadList.add(thread);
            thread.start();
        }
        // 等待所有线程运行结束
        for (Thread thread : threadList) {
            thread.join();
        }
        System.out.println("卖出总票数 = " + sum.get());
        System.out.println("剩余票数 = " + window.count);
        // 总票数 = 卖出总票数 + 剩余票数 = 1000
        System.out.println("总票数 = " + (sum.get() + window.count));
    }

    /**
     * 生成随机数
     *
     * @return 1-5
     */
    public static int randomAmount() {
        return ThreadLocalRandom.current().nextInt(5) + 1;
    }

    /**
     * 售票
     * count是共享变量
     *
     * @param amount 购买数量
     * @return 购买数量
     */
    public synchronized int sell(int amount) {
        if (count >= amount) {
            count -= amount;
            return amount;
        }
        return 0;
    }
}
