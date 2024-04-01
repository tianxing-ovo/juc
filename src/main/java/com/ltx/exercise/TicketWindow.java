package com.ltx.exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 售票窗口
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
        List<Thread> threadList = new ArrayList<>();
        int randomAmount = new Random().nextInt(5) + 1;
        AtomicInteger sum = new AtomicInteger();
        for (int i = 0; i < 200; i++) {
            Thread thread = new Thread(() -> sum.addAndGet(window.sell(randomAmount)));
            threadList.add(thread);
            thread.start();
        }
        // 等待所有线程运行结束
        for (Thread thread : threadList) {
            thread.join();
        }
        System.out.println(sum.get() + window.count);
    }

    /**
     * 售票
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
