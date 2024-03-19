package com.ltx.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 创建线程
 */
@Slf4j(topic = "test")
public class CreateThread {
    public static void main(String[] args) throws Exception {
        MyThread t1 = new MyThread("t1");
        t1.start();
        // 如果Runnable不为null,执行Runnable的run方法
        Thread t2 = new Thread(new MyRunnable(), "t2");
        t2.start();
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        Thread t3 = new Thread(futureTask, "t3");
        t3.start();
        log.info("{}", futureTask.get());
    }

    private static class MyThread extends Thread {
        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            log.info("继承Thread类");
        }
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            log.info("实现Runnable接口");
        }
    }

    private static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            log.info("实现Callable接口");
            Thread.sleep(1000);
            return 1;
        }
    }
}
