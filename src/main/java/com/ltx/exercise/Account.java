package com.ltx.exercise;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 账户类
 *
 * @author tianxing
 */
@Getter
@NoArgsConstructor
public class Account {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * 账户唯一ID(确定加锁顺序避免死锁)
     */
    private final int id = ID_GENERATOR.incrementAndGet();

    private int money;

    public Account(int money) {
        this.money = money;
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
     * 转账(this.money和target.money是共享变量)
     *
     * @param target 目标账户
     * @param amount 转账金额
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void transfer(Account target, int amount) {
        if (this == target) {
            return;
        }
        // 按账户ID大小确定加锁顺序
        Account first = this.id < target.id ? this : target;
        Account second = this.id < target.id ? target : this;
        synchronized (first) {
            synchronized (second) {
                if (this.money >= amount) {
                    this.money -= amount;
                    target.money += amount;
                }
            }
        }
    }
}
