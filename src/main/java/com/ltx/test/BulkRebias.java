package com.ltx.test;

import com.ltx.exercise.Account;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量重偏向
 *
 * @author tianxing
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
@Slf4j
public class BulkRebias {

    /**
     * 虚拟机选项: -XX:BiasedLockingStartupDelay=0 -XX:+UnlockDiagnosticVMOptions -XX:+PrintBiasedLockingStatistics
     * 批量重偏向阈值: 20
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        List<Account> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Account account = new Account();
            list.add(account);
            synchronized (account) {
                // 偏向锁
                log.info(ClassLayout.parseInstance(account).toPrintable());
            }
        }
        System.out.println("====================================");
        new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                synchronized (list.get(i)) {
                    log.info("i = {}", i);
                    // 前19次为轻量级锁 -> 第20次为偏向锁
                    log.info(ClassLayout.parseInstance(list.get(i)).toPrintable());
                }
            }
        }).start();
    }
}
