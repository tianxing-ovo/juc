package com.ltx;

import com.ltx.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 原子类测试
 *
 * @author tianxing
 */
@Slf4j
class AtomicTest {

    // 预期数组值
    private static final String EXPECTED_ARRAY =
            "[10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]";
    // 原子引用字段更新器
    private static final AtomicReferenceFieldUpdater<Student, String> updater =
            AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
    // 普通整型数组
    private final int[] array = new int[10];
    // 原子整型数组
    private final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
    // 原子累加器
    private final LongAdder longAdder = new LongAdder();

    @Test
    @DisplayName("不安全的数组自增")
    void unsafeArrayIncrement() {
        List<Thread> list = new ArrayList<>();
        // 创建10个线程
        for (int i = 0; i < 10; i++) {
            list.add(new Thread(() -> {
                // 对数组的每个元素自增1000次
                for (int j = 0; j < 10000; j++) {
                    array[j % 10]++;
                }
            }));
        }
        list.forEach(Thread::start);
        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // array: [4533, 4546, 4524, 4522, 4531, 4538, 4503, 4507, 4517, 4485]
        log.info("array: {}", Arrays.toString(array));
        assertNotEquals(EXPECTED_ARRAY, Arrays.toString(array));
    }

    @Test
    @DisplayName("原子整数数组自增")
    void atomicIntegerArrayIncrement() {
        List<Thread> list = new ArrayList<>();
        // 创建10个线程
        for (int i = 0; i < 10; i++) {
            list.add(new Thread(() -> {
                // 对原子数组的每个元素自增1000次
                for (int j = 0; j < 10000; j++) {
                    atomicIntegerArray.incrementAndGet(j % 10);
                }
            }));
        }
        list.forEach(Thread::start);
        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // atomicIntegerArray: [10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]
        log.info("atomicIntegerArray: {}", atomicIntegerArray);
        assertEquals(EXPECTED_ARRAY, atomicIntegerArray.toString());
    }

    @Test
    @DisplayName("原子引用字段更新器")
    void atomicReferenceFieldUpdater() {
        Student student = new Student();
        boolean success = updater.compareAndSet(student, null, "张三");
        log.info("student: {}", student);
        assertTrue(success);
        assertEquals("张三", student.name);
        boolean fail = updater.compareAndSet(student, null, "李四");
        assertFalse(fail);
        assertEquals("张三", student.name);
    }

    @Test
    @DisplayName("原子累加器")
    void longAdderSum() {
        List<Thread> list = new ArrayList<>();
        // 创建4个线程
        for (int i = 0; i < 4; i++) {
            list.add(new Thread(() -> {
                // 对原子累加器累加10000次
                for (int j = 0; j < 10000; j++) {
                    longAdder.increment();
                }
            }));
        }
        list.forEach(Thread::start);
        list.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // longAdder: 40000
        log.info("longAdder: {}", longAdder.sum());
        assertEquals(40000L, longAdder.sum());
    }
}
