package com.ltx.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ABA问题测试(A -> B -> A)
 *
 * @author tianxing
 */
@Slf4j
class ABATest {

    @Test
    @DisplayName("使用AtomicReference演示ABA问题")
    void atomicReference() throws InterruptedException {
        AtomicReference<String> ref = new AtomicReference<>("A");
        String expectedValue = ref.get();
        Thread t1 = new Thread(() -> {
            String prev = ref.get();
            boolean success = ref.compareAndSet(prev, "B");
            logCasResult("A -> B", success);
            if (!success) {
                return;
            }
            prev = ref.get();
            success = ref.compareAndSet(prev, "A");
            logCasResult("B -> A", success);
        }, "t1");
        t1.start();
        t1.join();
        boolean success = ref.compareAndSet(expectedValue, "C");
        logCasResult("A -> C", success);
        assertTrue(success, "未能复现ABA问题");
    }

    @Test
    @DisplayName("使用AtomicStampedReference解决ABA问题")
    void atomicStampedReference() throws InterruptedException {
        AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
        int[] stampHolder = new int[1];
        String expectedReference = ref.get(stampHolder);
        int expectedStamp = stampHolder[0];
        log.info("expectedStamp: {}", expectedStamp);
        // 创建线程t1
        Thread t1 = createThread(ref);
        t1.join();
        boolean success = ref.compareAndSet(expectedReference, "C", expectedStamp, expectedStamp + 1);
        logCasResult("A -> C", success);
        assertFalse(success, "未能解决ABA问题");
    }

    private Thread createThread(AtomicStampedReference<String> ref) {
        Thread t1 = new Thread(() -> {
            int[] holder = new int[1];
            String reference = ref.get(holder);
            int stamp = holder[0];
            boolean success = ref.compareAndSet(reference, "B", stamp, stamp + 1);
            logCasResult("A -> B", success);
            if (!success) {
                return;
            }
            log.info("stamp: {}", ref.getStamp());
            reference = ref.get(holder);
            stamp = holder[0];
            success = ref.compareAndSet(reference, "A", stamp, stamp + 1);
            logCasResult("B -> A", success);
            if (!success) {
                return;
            }
            log.info("stamp: {}", ref.getStamp());
        }, "t1");
        t1.start();
        return t1;
    }

    @Test
    @DisplayName("使用AtomicMarkableReference演示倒垃圾问题")
    void atomicMarkableReference() throws InterruptedException {
        AtomicMarkableReference<String> ref = new AtomicMarkableReference<>("垃圾袋", true);
        boolean[] markHolder = new boolean[1];
        String expectedReference = ref.get(markHolder);
        boolean expectedMark = markHolder[0];
        log.info("expectedMark: {}", expectedMark);
        Thread t1 = new Thread(() -> {
            boolean[] holder = new boolean[1];
            String reference = ref.get(holder);
            boolean mark = holder[0];
            // 倒垃圾(不换垃圾袋)
            if (ref.compareAndSet(reference, reference, mark, false)) {
                log.info("倒垃圾成功");
            } else {
                log.info("倒垃圾失败");
            }
        }, "t1");
        t1.start();
        t1.join();
        // 换垃圾袋
        boolean success = ref.compareAndSet(expectedReference, "新垃圾袋", expectedMark, false);
        if (success) {
            log.info("换垃圾袋成功");
        } else {
            log.info("换垃圾袋失败");
        }
        assertFalse(success, "垃圾已倒但仍换了新垃圾袋");
    }

    /**
     * 记录CAS操作结果
     *
     * @param action  操作描述
     * @param success 是否成功
     */
    private void logCasResult(String action, boolean success) {
        log.info("{} {}", action, success ? "success" : "failed");
    }
}
