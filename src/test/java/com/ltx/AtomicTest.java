package com.ltx;

import com.ltx.entity.Student;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAdder;

/**
 * 原子类测试
 *
 * @author tianxing
 */
public class AtomicTest {
    // 原子引用字段更新器
    private static final AtomicReferenceFieldUpdater<Student, String> updater =
            AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
    // 原子数组
    private final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
    private final int[] array = new int[10];
    // 原子累加器
    private final LongAdder longAdder = new LongAdder();

    @Test

    public void testArray() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Thread(() -> {
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
        System.out.println(Arrays.toString(array));
    }

    @Test
    public void testAtomicIntegerArray() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Thread(() -> {
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
        System.out.println(atomicIntegerArray);
    }

    @Test
    public void testAtomicReferenceFieldUpdater() {
        Student student = new Student();
        if (updater.compareAndSet(student, null, "张三")) {
            System.out.println(student);
        }
    }

    @Test
    public void testLongAdder() {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new Thread(() -> {
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
        System.out.println(longAdder.sum());
    }
}
