package com.ltx.exercise;

import com.ltx.entity.Chopstick;
import com.ltx.entity.Philosopher;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 哲学家就餐问题
 * 饥饿: 一个线程由于优先级太低始终得不到CPU调度执行
 *
 * @author tianxing
 */
public class DiningPhilosophersProblem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1-死锁 2-饥饿 3-解决死锁");
        int i = scanner.nextInt();
        Chopstick c1 = new Chopstick("筷子1");
        Chopstick c2 = new Chopstick("筷子2");
        Chopstick c3 = new Chopstick("筷子3");
        Chopstick c4 = new Chopstick("筷子4");
        Chopstick c5 = new Chopstick("筷子5");
        Philosopher socrates = new Philosopher("苏格拉底", c1, c2);
        Philosopher plato = new Philosopher("柏拉图", c2, c3);
        Philosopher aristotle = new Philosopher("亚里士多德", c3, c4);
        Philosopher heraclitus = new Philosopher("赫拉克利特", c4, c5);
        Philosopher archimedes = new Philosopher("阿基米德", c5, c1);
        if (i == 2) {
            archimedes = new Philosopher("阿基米德", c1, c5);
        } else if (i == 3) {
            socrates.setRunnable(socrates::reentrantLock);
            plato.setRunnable(plato::reentrantLock);
            aristotle.setRunnable(aristotle::reentrantLock);
            heraclitus.setRunnable(heraclitus::reentrantLock);
            archimedes.setRunnable(archimedes::reentrantLock);
        }
        Arrays.asList(socrates, plato, aristotle, heraclitus, archimedes).forEach(Philosopher::start);
    }
}
