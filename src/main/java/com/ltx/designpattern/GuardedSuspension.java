package com.ltx.designpattern;

import com.ltx.entity.GuardedObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保护性暂停模式: 生产者线程和消费者线程是一对一的关系
 *
 * @author tianxing
 */
@SuppressWarnings("SpellCheckingInspection")
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
