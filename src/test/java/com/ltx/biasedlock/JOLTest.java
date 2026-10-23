package com.ltx.biasedlock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.vm.VM;

/**
 * JOL(Java Object Layout)测试
 *
 * @author tianxing
 */
class JOLTest {

    static {
        System.setProperty("jol.skipHotspotSAAttach", "true");
        System.setProperty("jol.skipDynamicAttach", "true");
    }

    @Test
    @DisplayName("打印当前虚拟机的信息")
    void printVmDetails() {
        System.out.println(VM.current().details());
    }
}
