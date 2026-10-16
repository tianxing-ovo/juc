package com.ltx.exercise;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 售票窗口测试
 *
 * @author tianxing
 */
@Slf4j(topic = "TicketWindowTest")
class TicketWindowTest {

    /**
     * 初始总票数
     */
    private static final int INITIAL_COUNT = 1000;

    /**
     * 购票线程数
     */
    private static final int BUYER_COUNT = 200;

    @RepeatedTest(value = 10, name = "第{currentRepetition}次运行(共{totalRepetitions}次)")
    @DisplayName("并发售票")
    void concurrentSell(RepetitionInfo repetitionInfo) throws InterruptedException {
        TicketWindow window = new TicketWindow(INITIAL_COUNT);
        // 所有购票线程的集合
        List<Thread> threadList = new ArrayList<>();
        // 卖出总票数
        AtomicInteger sum = new AtomicInteger();
        // 创建并启动所有购票线程
        for (int i = 0; i < BUYER_COUNT; i++) {
            Thread thread = new Thread(() -> {
                // 随机生成购票数量
                int amount = TicketWindow.randomAmount();
                // 售票
                int actualAmount = window.sell(amount);
                // 累加卖出总票数
                sum.addAndGet(actualAmount);
            });
            // 添加到线程集合
            threadList.add(thread);
            // 启动线程
            thread.start();
        }
        // 等待所有购票线程运行结束
        for (Thread thread : threadList) {
            thread.join();
        }
        // 获取卖出总票数
        int soldCount = sum.get();
        // 获取剩余票数
        int remainingCount = window.getCount();
        // 获取总票数
        int total = soldCount + remainingCount;
        log.info("[{}/{}] 卖出总票数 = {}, 剩余票数 = {}, 总票数 = {}", 
        repetitionInfo.getCurrentRepetition(), 
        repetitionInfo.getTotalRepetitions(), soldCount, remainingCount, total);
        // 断言总票数等于初始总票数
        assertEquals(INITIAL_COUNT, total);
    }
}
