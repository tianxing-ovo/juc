package com.ltx.exercise;

import com.ltx.entity.Chopstick;
import com.ltx.entity.Philosopher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 哲学家就餐测试
 *
 * @author tianxing
 */
@Slf4j(topic = "DiningPhilosophersTest")
class DiningPhilosophersTest {

    /**
     * 运行观察时长(秒)
     */
    private static final int RUN_SECONDS = 3;

    private final Chopstick c1 = new Chopstick("筷子1");
    private final Chopstick c2 = new Chopstick("筷子2");
    private final Chopstick c3 = new Chopstick("筷子3");
    private final Chopstick c4 = new Chopstick("筷子4");
    private final Chopstick c5 = new Chopstick("筷子5");

    /**
     * 锁内存标识与筷子名称的映射表
     */
    private final Map<String, String> chopstickMap = Stream.of(c1, c2, c3, c4, c5)
            .collect(Collectors.toMap(
                    c -> c.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(c)),
                    Chopstick::getName
            ));

    @Test
    @DisplayName("死锁")
    void deadlock() throws InterruptedException {
        // 创建哲学家列表
        List<Philosopher> philosophers = createPhilosophers(false);
        // 启动所有哲学家线程
        philosophers.forEach(Philosopher::start);
        // 获取JVM线程管理器
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 初始化死锁线程ID数组
        long[] deadlockedThreadIds = null;
        // 设置超时时间(3秒)
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(RUN_SECONDS);
        // 每隔50ms轮询一次等待死锁形成
        while (System.currentTimeMillis() < deadline) {
            // 查找死锁线程
            deadlockedThreadIds = threadMXBean.findDeadlockedThreads();
            // 如果检测到死锁线程则跳出循环
            if (deadlockedThreadIds != null) {
                break;
            }
            // 等待50ms后继续轮询
            TimeUnit.MILLISECONDS.sleep(50);
        }
        // 断言存在死锁
        assertNotNull(deadlockedThreadIds, "未检测到死锁线程");
        assertEquals(5, deadlockedThreadIds.length, "死锁线程数量不匹配");
        // 获取死锁线程的详细信息
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedThreadIds);
        for (ThreadInfo info : threadInfos) {
            log.info("哲学家 [{}] 阻塞等待 [{}], 当前持有者为 [{}]",
                    info.getThreadName(),
                    resolveChopstickName(info.getLockName()),
                    info.getLockOwnerName());
        }
        log.info("死锁场景演示结束");
    }

    @Test
    @DisplayName("饥饿")
    void starvation() throws InterruptedException {
        // 创建哲学家列表并反转阿基米德拿筷子顺序
        List<Philosopher> philosophers = createPhilosophers(true);
        // 启动所有哲学家线程
        philosophers.forEach(Philosopher::start);
        // 运行指定时间观察就餐输出
        TimeUnit.SECONDS.sleep(RUN_SECONDS);
        // 打印所有哲学家的就餐次数
        philosophers.forEach(p -> log.info("哲学家 [{}] 累计就餐 [{}] 次", p.getName(), p.getEatCount()));
        // 断言存在就餐次数为0的饥饿哲学家
        boolean hasStarvation = philosophers.stream()
                .anyMatch(p -> p.getEatCount() == 0);
        assertTrue(hasStarvation, "未检测到饥饿现象");
        log.info("饥饿场景演示结束");
    }

    @Test
    @DisplayName("解决死锁")
    void solveDeadlock() throws InterruptedException {
        // 创建哲学家列表
        List<Philosopher> philosophers = createPhilosophers(false);
        // 设置所有哲学家使用ReentrantLock
        philosophers.forEach(p -> p.setUseReentrantLock(true));
        // 启动所有哲学家线程
        philosophers.forEach(Philosopher::start);
        // 运行指定时间观察就餐输出
        TimeUnit.SECONDS.sleep(RUN_SECONDS);
        // 断言未发生死锁
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreadIds = threadMXBean.findDeadlockedThreads();
        assertNull(deadlockedThreadIds, "检测到死锁线程");
        log.info("解决死锁场景演示结束");
    }

    /**
     * 创建哲学家集合
     *
     * @param reverseArchimedes 是否反转阿基米德拿筷子顺序
     * @return 哲学家集合
     */
    private List<Philosopher> createPhilosophers(boolean reverseArchimedes) {
        Chopstick archimedesLeft = reverseArchimedes ? c1 : c5;
        Chopstick archimedesRight = reverseArchimedes ? c5 : c1;
        return Arrays.asList(
                new Philosopher("苏格拉底", c1, c2),
                new Philosopher("柏拉图", c2, c3),
                new Philosopher("亚里士多德", c3, c4),
                new Philosopher("赫拉克利特", c4, c5),
                new Philosopher("阿基米德", archimedesLeft, archimedesRight)
        );
    }

    /**
     * 根据锁标识直接查表获取筷子名称
     *
     * @param lockName 锁对象标识
     * @return 筷子名称
     */
    private String resolveChopstickName(String lockName) {
        return chopstickMap.getOrDefault(lockName, lockName);
    }
}
