package com.ltx.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * 撤销偏向锁
 *
 * @author tianxing
 */
@Slf4j
public class RevokeBias {
    private static final Object OBJECT1 = new Object();
    private static final Object OBJECT2 = new Object();
    private static final Object OBJECT3 = new Object();
    private static final Object OBJECT4 = new Object();

    /**
     * 虚拟机选项: -XX:BiasedLockingStartupDelay=0
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        revokeBiasInUnlockedState();
        System.out.println("========================================");
        revokeBiasInLockedState();
        System.out.println("========================================");
        revokeBiasInNewThread();
        System.out.println("========================================");
        revokeBiasByInvokeWait();
    }

    /**
     * 未锁定状态下调用hashcode方法撤销偏向锁 -> 升级为轻量级锁
     */
    public static void revokeBiasInUnlockedState() {
        // 计算对象的哈希值(76ccd017) -> 对象再也无法进入偏向锁状态
        System.out.println(Integer.toHexString(OBJECT1.hashCode()));
        // Mark Word: 0x00000076ccd01701 (hash: 0x76ccd017; age: 0)
        System.out.println(ClassLayout.parseInstance(OBJECT1).toPrintable());
        synchronized (OBJECT1) {
            // Mark Word: 0x0000002e9f2ff6d0 (thin lock: 0x0000002e9f2ff6d0)
            System.out.println(ClassLayout.parseInstance(OBJECT1).toPrintable());
        }
    }

    /**
     * 锁定状态下调用hashcode方法撤销偏向锁 -> 升级为重量级锁
     */
    public static void revokeBiasInLockedState() {
        synchronized (OBJECT2) {
            // Mark Word: 0x0000020029c38005 (biased: 0x00000000800a70e0; epoch: 0; age: 0)
            System.out.println(ClassLayout.parseInstance(OBJECT2).toPrintable());
            // 计算对象的哈希值(7d0587f1) -> 膨胀为重量级锁
            System.out.println(Integer.toHexString(OBJECT2.hashCode()));
            // Mark Word: 0x0000020044aa87ca (fat lock: 0x0000020044aa87ca)
            System.out.println(ClassLayout.parseInstance(OBJECT2).toPrintable());
        }
    }

    /**
     * 新线程撤销偏向锁 -> 升级为轻量级锁
     */
    @SneakyThrows
    public static void revokeBiasInNewThread() {
        synchronized (OBJECT3) {
            // Mark Word: 0x000002c7fe129005 (biased: 0x00000000b1ff84a4; epoch: 0; age: 0)
            log.info(ClassLayout.parseInstance(OBJECT3).toPrintable());
        }
        Thread t = new Thread(() -> {
            synchronized (OBJECT3) {
                // Mark Word: 0x000000d70cffecb8 (thin lock: 0x000000d70cffecb8)
                log.info(ClassLayout.parseInstance(OBJECT3).toPrintable());
            }
        }, "t");
        t.start();
        t.join();
    }

    /**
     * 调用wait/notify撤销偏向锁 -> 升级为重量级锁
     */
    @SneakyThrows
    public static void revokeBiasByInvokeWait() {
        synchronized (OBJECT4) {
            // Mark Word: 0x0000019370cd9005 (biased: 0x0000000064dc3364; epoch: 0; age: 0)
            log.info(ClassLayout.parseInstance(OBJECT4).toPrintable());
            OBJECT4.wait(1000);
            // Mark Word: 0x000001937ebb7a5a (fat lock: 0x000001937ebb7a5a)
            log.info(ClassLayout.parseInstance(OBJECT4).toPrintable());
        }
    }
}
