package com.ltx.exercise;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 账户测试
 *
 * @author tianxing
 */
@Slf4j(topic = "AccountTest")
class AccountTest {

    /**
     * 初始金额
     */
    private static final int INITIAL_MONEY = 1000;

    /**
     * 转账次数
     */
    private static final int TRANSFER_COUNT = 1000;

    @RepeatedTest(value = 10, name = "第{currentRepetition}次运行(共{totalRepetitions}次)")
    @DisplayName("并发转账")
    void concurrentTransfer(RepetitionInfo repetitionInfo) throws InterruptedException {
        // 创建账户a
        Account a = new Account(INITIAL_MONEY);
        // 创建账户b
        Account b = new Account(INITIAL_MONEY);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < TRANSFER_COUNT; i++) {
                // 账户a向账户b转账
                a.transfer(b, Account.randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < TRANSFER_COUNT; i++) {
                // 账户b向账户a转账
                b.transfer(a, Account.randomAmount());
            }
        }, "t2");
        // 启动转账线程
        t1.start();
        t2.start();
        // 等待所有转账线程运行结束
        t1.join();
        t2.join();
        // 获取账户a的余额
        int aMoney = a.getMoney();
        // 获取账户b的余额
        int bMoney = b.getMoney();
        // 获取总金额
        int total = aMoney + bMoney;
        log.info("[{}/{}] 账户a余额 = {}, 账户b余额 = {}, 总金额 = {}",
                repetitionInfo.getCurrentRepetition(),
                repetitionInfo.getTotalRepetitions(),
                aMoney, bMoney, total);
        // 断言总金额等于初始金额之和
        assertEquals(INITIAL_MONEY * 2, total);
    }
}
