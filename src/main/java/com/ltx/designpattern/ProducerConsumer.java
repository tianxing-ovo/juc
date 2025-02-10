package com.ltx.designpattern;

import com.ltx.entity.Message;
import com.ltx.entity.MessageQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 生产者消费者模式
 *
 * @author tianxing
 */
@Slf4j(topic = "ProducerConsumer")
public class ProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        MessageQueue messageQueue = new MessageQueue(2);
        for (int i = 1; i <= 3; i++) {
            int id = i;
            new Thread(() -> messageQueue.put(new Message(id, "value" + id)), "producer" + i).start();
        }
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    messageQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "consumer").start();
    }
}


