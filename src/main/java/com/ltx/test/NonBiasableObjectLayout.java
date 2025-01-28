package com.ltx.test;

import org.openjdk.jol.info.ClassLayout;

/**
 * 不可偏向的对象布局
 *
 * @author tianxing
 */
public class NonBiasableObjectLayout {
    private static final Object OBJECT = new Object();

    /**
     * 虚拟机选项: -XX:-UseBiasedLocking
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        // Mark Word: 0x0000000000000001 (non-biasable; age: 0)
        System.out.println(ClassLayout.parseInstance(OBJECT).toPrintable());
        // 计算对象的哈希值(4d49af10) -> 第一次用到hashCode时才会赋值
        System.out.println(Integer.toHexString(OBJECT.hashCode()));
        // Mark Word: 0x0000004d49af1001 (hash: 0x4d49af10; age: 0)
        System.out.println(ClassLayout.parseInstance(OBJECT).toPrintable());
        synchronized (OBJECT) {
            // Mark Word: 0x000000b56fbfe2f0 (thin lock: 0x000000b56fbfe2f0)
            System.out.println(ClassLayout.parseInstance(OBJECT).toPrintable());
        }
    }
}
