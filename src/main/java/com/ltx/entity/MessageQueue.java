package com.ltx.entity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 消息队列
 *
 * @author tianxing
 */
@AllArgsConstructor
@Slf4j(topic = "MessageQueue")
public class MessageQueue {

    private final Deque<Message> deque = new LinkedBlockingDeque<>();

    private int capacity;

    /**
     * 获取消息
     */
    public void take() {
        synchronized (deque) {
            // 检查队列是否为空
            while (deque.isEmpty()) {
                try {
                    log.info("队列为空等待生产者生产消息");
                    deque.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Message message = deque.removeFirst();
            log.info("已消费消息: {}", message);
            deque.notifyAll();
        }
    }

    /**
     * 存入消息
     *
     * @param message 消息
     */
    public void put(Message message) {
        synchronized (deque) {
            // 检查队列是否已满
            while (deque.size() == capacity) {
                try {
                    log.info("队列已满等待消费者消费消息");
                    deque.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            deque.addLast(message);
            log.info("已生产消息: {}", message);
            deque.notifyAll();
        }
    }

}
