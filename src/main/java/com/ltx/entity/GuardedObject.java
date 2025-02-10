package com.ltx.entity;

import lombok.Getter;

/**
 * 受保护的对象
 * 虚假唤醒(spurious wakeup): 线程在不满足条件的情况下被错误地唤醒
 *
 * @author tianxing
 */
@Getter
public class GuardedObject {
    // 唯一标识
    private final int id;
    // 结果
    private Object response;

    public GuardedObject(int id) {
        this.id = id;
    }

    /**
     * 获取结果
     *
     * @param timeout 超时时间
     * @return 结果
     */
    public Object get(long timeout) {
        synchronized (this) {
            // 开始时间
            long start = System.currentTimeMillis();
            // 经历的时间
            long elapse = 0;
            while (response == null) {
                try {
                    // 剩余的等待时间 -> 防止虚假唤醒
                    long wait = timeout - elapse;
                    if (wait <= 0) {
                        break;
                    }
                    this.wait(wait);
                    elapse = System.currentTimeMillis() - start;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return response;
        }
    }

    /**
     * 设置结果
     *
     * @param response 结果
     */
    public void set(Object response) {
        synchronized (this) {
            this.response = response;
            this.notifyAll();
        }
    }
}
