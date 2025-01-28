package com.ltx;

import org.junit.Test;
import org.openjdk.jol.vm.VM;

/**
 * JOL(Java Object Layout)测试
 *
 * @author tianxing
 */
public class JOLTest {
    @Test
    public void test() {
        // 打印当前虚拟机的信息
        System.out.println(VM.current().details());
    }
}
