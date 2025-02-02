package com.ltx.designpattern;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保护性暂停模式: 生产者线程和消费者线程是一对一的关系
 * 虚假唤醒(spurious wakeup): 线程在没有收到显式通知的情况下意外醒来
 *
 * @author tianxing
 */
@Slf4j
public class GuardedSuspension {

    private static final Map<Integer, GuardedObject> GUARDED_OBJECT_MAP = new ConcurrentHashMap<>();

    private static int id = 1;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                GuardedObject guardedObject = createGuardedObject();
                log.info("begin to get response{}", guardedObject.getId());
                Object response = guardedObject.get(5000);
                log.info("get response: {}", response);
            }, "consumer" + i).start();
        }
        Thread.sleep(1000);
        for (Integer id : getIds()) {
            new Thread(() -> {
                GuardedObject guardedObject = getGuardedObject(id);
                log.info("begin to set response{}", id);
                guardedObject.set("response" + id);
            }, "producer" + id).start();
        }
    }

    /**
     * 生成id
     *
     * @return id
     */
    private static synchronized int generateId() {
        return id++;
    }

    /**
     * 获取所有id
     *
     * @return id集合
     */
    private static Set<Integer> getIds() {
        return GUARDED_OBJECT_MAP.keySet();
    }

    /**
     * 创建GuardedObject
     *
     * @return GuardedObject
     */
    private static GuardedObject createGuardedObject() {
        GuardedObject guardedObject = new GuardedObject(generateId());
        GUARDED_OBJECT_MAP.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    /**
     * 获取GuardedObject
     *
     * @param id id
     * @return GuardedObject
     */
    private static GuardedObject getGuardedObject(int id) {
        return GUARDED_OBJECT_MAP.remove(id);
    }
}

@Getter
class GuardedObject {
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