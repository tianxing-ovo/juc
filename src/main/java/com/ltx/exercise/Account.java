package com.ltx.exercise;

import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 帐户
 *
 * @author tianxing
 */
@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor
public class Account {

    private int money;

    public Account(int money) {
        this.money = money;
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomAmount());
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 输出账户a的余额
        System.out.println("a.money = " + a.money);
        // 输出账户b的余额
        System.out.println("b.money = " + b.money);
        // 输出账户a和b的总余额
        System.out.println("total = " + (a.money + b.money));
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
     * 转账
     * this.money和target.money是共享变量
     *
     * @param target 目标账户
     * @param amount 转账金额
     */
    public void transfer(Account target, int amount) {
        if (this.money >= amount) {
            synchronized (Account.class) {
                this.money -= amount;
                target.money += amount;
            }
        }
    }
}
