package com.ltx.exercise;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 售票窗口
 *
 * @author tianxing
 */
@Getter
@Slf4j(topic = "TicketWindow")
public class TicketWindow {

    /**
     * 余票数
     */
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    /**
     * 生成随机数
     *
     * @return 1-5
     */
    public static int randomAmount() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }

    /**
     * 售票(count是共享变量)
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
