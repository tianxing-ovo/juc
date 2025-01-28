package com.ltx.test;

import com.ltx.exercise.Account;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量撤销
 *
 * @author tianxing
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
@Slf4j
public class BulkRevoke {
    /**
     * 虚拟机选项: -XX:BiasedLockingStartupDelay=0 -XX:+UnlockDiagnosticVMOptions -XX:+PrintBiasedLockingStatistics
     * 批量撤销阈值: 40
     *
     * @param args 参数
     */
    public static void main(String[] args) throws InterruptedException {
        List<Account> list = new ArrayList<>();
        int n = 39;
        for (int i = 0; i < n; i++) {
            Account account = new Account();
            list.add(account);
            synchronized (account) {
                // 偏向锁
                log.info(ClassLayout.parseInstance(account).toPrintable());
            }
        }
        System.out.println("====================================");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < n; i++) {
                synchronized (list.get(i)) {
                    log.info("i = {}", i);
                    // 前19次为轻量级锁 -> 20次-39次为偏向锁
                    // 撤销次数: 20
                    log.info(ClassLayout.parseInstance(list.get(i)).toPrintable());
                }
            }
        }, "t1");
        t1.start();
        t1.join();
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < n; i++) {
                synchronized (list.get(i)) {
                    log.info("i = {}", i);
                    // 前19次为轻量级锁 -> 20次-39次为轻量级锁
                    // 撤销次数: 39-19=20
                    log.info(ClassLayout.parseInstance(list.get(i)).toPrintable());
                }
            }
        }, "t2");
        t2.start();
        t2.join();
        System.out.println("====================================");
        // Mark Word: 0x0000000000000001 (non-biasable; age: 0)
        log.info(ClassLayout.parseInstance(new Account()).toPrintable());
    }
}
