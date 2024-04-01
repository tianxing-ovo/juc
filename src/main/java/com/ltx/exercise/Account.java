package com.ltx.exercise;

import java.util.Random;

/**
 * 帐户
 */
public class Account {

    private int money;

    public Account(int money) {
        this.money = money;
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        int randomAmount = new Random().nextInt(5) + 1;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomAmount);
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomAmount);
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(a.money + b.money);
    }

    /**
     * 转账
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
