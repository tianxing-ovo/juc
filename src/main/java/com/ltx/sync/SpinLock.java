package com.ltx.sync;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用CAS实现自旋锁
 *
 * @author tianxing
 */
@Slf4j(topic = "SpinLock")
public class SpinLock {

    // 锁的持有者
    private final AtomicReference<Thread> owner = new AtomicReference<>(null);
    // 重入计数器
    private int state = 0;

    public static void main(String[] args) throws InterruptedException {
        SpinLock lock = new SpinLock();
        new Thread(() -> {
            log.info("尝试加锁");
            lock.lock();
            try {
                log.info("首次加锁成功");
                // 可重入加锁
                lock.lock();
                try {
                    log.info("重入加锁成功");
                    TimeUnit.SECONDS.sleep(1);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "t1").start();
        // 保证t1先获取锁
        TimeUnit.MILLISECONDS.sleep(10);
        new Thread(() -> {
            log.info("尝试加锁");
            lock.lock();
            try {
                log.info("加锁成功");
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

    public void lock() {
        Thread current = Thread.currentThread();
        // 可重入加锁
        if (current == owner.get()) {
            state++;
            return;
        }
        // CAS自旋获取锁
        while (!owner.compareAndSet(null, current)) {
            // 优化CPU自旋并降低功耗
            Thread.onSpinWait();
        }
        state = 1;
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        // 校验释放者身份
        if (current != owner.get()) {
            throw new IllegalMonitorStateException("当前线程未持有锁: " + current.getName());
        }
        state--;
        if (state == 0) {
            log.info("释放锁");
            owner.set(null);
        }
    }
}
