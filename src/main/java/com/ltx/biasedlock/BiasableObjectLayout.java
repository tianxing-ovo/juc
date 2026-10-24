package com.ltx.biasedlock;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 可偏向的对象布局
 *
 * @author tianxing
 */
@Slf4j(topic = "BiasableObjectLayout")
public class BiasableObjectLayout {

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

    /**
     * 虚拟机选项: -XX:-PrintWarnings -XX:+UseBiasedLocking
     *
     * @param args 程序实参
     */
    public static void main(String[] args) {
        // 加锁前: 处于匿名偏向状态(可偏向但未偏向任何线程)
        // Mark Word: 0x0000000000000005 (biasable; age: 0)
        log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
        synchronized (OBJECT) {
            // 加锁中: 偏向当前main线程
            // Mark Word: 0x000001fd853fa005 (biased: 0x000000007f614fe8; epoch: 0; age: 0)
            log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
        }
        // 退出同步代码块: 依然偏向当前main线程(不会主动释放偏向)
        // Mark Word: 0x000001fd853fa005 (biased: 0x000000007f614fe8; epoch: 0; age: 0)
        log.info("\n{}", ClassLayout.parseInstance(OBJECT).toPrintable());
    }
}
