package com.ltx.test;

import org.openjdk.jol.info.ClassLayout;

/**
 * 可偏向的对象布局
 *
 * @author tianxing
 */
public class BiasableObjectLayout {

    private static final Object OBJECT = new Object();

    /**
     * 虚拟机选项: -XX:BiasedLockingStartupDelay=0
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        // Mark Word: 0x0000000000000005 (biasable; age: 0)
        System.out.println(ClassLayout.parseInstance(OBJECT).toPrintable());
        synchronized (OBJECT) {
            // Mark Word: 0x000001fd853fa005 (biased: 0x000000007f614fe8; epoch: 0; age: 0)
            System.out.println(ClassLayout.parseInstance(OBJECT).toPrintable());
        }
    }
}
