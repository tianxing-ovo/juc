package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ABA问题: A -> B -> A
 *
 * @author tianxing
 */
@SuppressWarnings("SpellCheckingInspection")
@Slf4j(topic = "ABA")
public class ABA {

    private static final AtomicReference<String> ATOMIC_REFERENCE = new AtomicReference<>("A");
    // initialStamp: 版本号
    private static final AtomicStampedReference<String> ATOMIC_STAMPED_REFERENCE = new AtomicStampedReference<>("A", 0);
    // initialMark: true-满的垃圾袋 false-空的垃圾袋
    private static final AtomicMarkableReference<String> ATOMIC_MARKABLE_REFERENCE = new AtomicMarkableReference<>("垃圾袋", true);

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1-AtomicReference 2-AtomicStampedReference 3-AtomicMarkableReference");
        int i = scanner.nextInt();
        if (i == 1) {
            reference();
        } else if (i == 2) {
            stampedReference();
        } else if (i == 3) {
            markableReference();
        }
    }

    /**
     * 使用AtomicReference演示ABA问题
     *
     * @throws InterruptedException 中断异常
     */
    public static void reference() throws InterruptedException {
        String expect = ATOMIC_REFERENCE.get();
        new Thread(() -> {
            log.info("A -> B");
            if (ATOMIC_REFERENCE.compareAndSet(ATOMIC_REFERENCE.get(), "B")) {
                log.info("A -> B success");
                log.info("B -> A");
                if (ATOMIC_REFERENCE.compareAndSet(ATOMIC_REFERENCE.get(), "A")) {
                    log.info("B -> A success");
                } else {
                    log.info("B -> A failed");
                }
            } else {
                log.info("A -> B failed");
            }
        }, "t1").start();
        Thread.sleep(1000);
        log.info("A -> C");
        if (ATOMIC_REFERENCE.compareAndSet(expect, "C")) {
            log.info("A -> C success");
        } else {
            log.info("A -> C failed");
        }
    }

    /**
     * 使用AtomicStampedReference解决ABA问题
     *
     * @throws InterruptedException 中断异常
     */
    public static void stampedReference() throws InterruptedException {
        String expectedReference = ATOMIC_STAMPED_REFERENCE.getReference();
        // 版本号
        int expectedStamp = ATOMIC_STAMPED_REFERENCE.getStamp();
        log.info("stamp: {}", expectedStamp);
        new Thread(() -> {
            log.debug("A -> B");
            String reference = ATOMIC_STAMPED_REFERENCE.getReference();
            int stamp = ATOMIC_STAMPED_REFERENCE.getStamp();
            if (ATOMIC_STAMPED_REFERENCE.compareAndSet(reference, "B", stamp, stamp + 1)) {
                log.debug("A -> B success");
                log.info("stamp: {}", ATOMIC_STAMPED_REFERENCE.getStamp());
                log.debug("B -> A");
                reference = ATOMIC_STAMPED_REFERENCE.getReference();
                stamp = ATOMIC_STAMPED_REFERENCE.getStamp();
                if (ATOMIC_STAMPED_REFERENCE.compareAndSet(reference, "A", stamp, stamp + 1)) {
                    log.debug("B -> A success");
                    log.debug("stamp: {}", ATOMIC_STAMPED_REFERENCE.getStamp());
                } else {
                    log.debug("B -> A failed");
                }
            } else {
                log.debug("A -> B failed");
            }
        }, "t1").start();
        Thread.sleep(1000);
        log.debug("A -> C");
        if (ATOMIC_STAMPED_REFERENCE.compareAndSet(expectedReference, "C", expectedStamp, expectedStamp + 1)) {
            log.debug("A -> C success");
        } else {
            log.debug("A -> C failed");
        }
    }

    /**
     * 使用AtomicMarkableReference演示倒垃圾问题
     */
    public static void markableReference() throws InterruptedException {
        String expectedReference = ATOMIC_MARKABLE_REFERENCE.getReference();
        new Thread(() -> {
            log.info("倒垃圾");
            String reference = ATOMIC_MARKABLE_REFERENCE.getReference();
            // 垃圾袋没换
            if (ATOMIC_MARKABLE_REFERENCE.compareAndSet(reference, reference, true, false)) {
                log.info("倒垃圾成功");
            } else {
                log.info("倒垃圾失败");
            }
        }, "t1").start();
        Thread.sleep(1000);
        log.info("换垃圾袋");
        if (ATOMIC_MARKABLE_REFERENCE.compareAndSet(expectedReference, "新垃圾袋", true, false)) {
            log.info("换垃圾袋成功");
        } else {
            log.info("换垃圾袋失败");
        }
    }
}
