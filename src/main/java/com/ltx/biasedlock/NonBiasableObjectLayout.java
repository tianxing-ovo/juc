package com.ltx.biasedlock;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 不可偏向的对象布局
 *
 * @author tianxing
 */
@Slf4j(topic = "NonBiasableObjectLayout")
public class NonBiasableObjectLayout {

    private static final Object OBJECT = new Object();

    static {
        System.setProperty("jol.skipHotspotSAAttach", "true");
        System.setProperty("jol.skipDynamicAttach", "true");
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            VM.current();
        } finally {
            System.setOut(originalOut);
        }
    }

    public static void main(String[] args) {
        // 加锁前: 处于未锁定状态(不可偏向)
        // Mark Word: 0x0000000000000001 (non-biasable; age: 0)
        log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
        // 计算对象的哈希值(4d49af10)(第一次用到hashCode时才会赋值)
        log.info("hashCode: 0x{}", Integer.toHexString(OBJECT.hashCode()));
        // Mark Word: 0x0000004d49af1001 (hash: 0x4d49af10; age: 0)
        log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
        synchronized (OBJECT) {
            // 加锁中: 升级为轻量级锁(指向main线程栈帧中的Lock Record)
            // Mark Word: 0x000000b56fbfe2f0 (thin lock: 0x000000b56fbfe2f0)
            log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
        }
        // 退出同步代码块: 恢复为未锁定状态(将栈帧中的Displaced Mark Word写回对象头)
        // Mark Word: 0x0000004d49af1001 (hash: 0x4d49af10; age: 0)
        log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
    }
}
