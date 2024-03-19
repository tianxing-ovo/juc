import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Interrupt {

    /**
     * 打断阻塞线程(sleep/wait/join),会清空打断标记
     */
    @Test
    public void interruptBlockingThread() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.info("enter sleep");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // false
                log.info("打断标记: {}", Thread.currentThread().isInterrupted());
            }
        }, "t1");
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("interrupt");
        // 打断正在睡眠的线程,sleep方法会抛出InterruptedException
        t1.interrupt();
    }

    /**
     * 打断正常运行的线程,不会清空打断标记
     */
    @Test
    public void interruptNormalThread() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("被打断了,退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        log.info("interrupt");
        t1.interrupt();
    }
}
