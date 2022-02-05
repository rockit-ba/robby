package cn.robby.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.robby.common.Config;
import cn.robby.common.pub_listener.events.TimeoutEvent;
import cn.robby.common.pub_listener.impl.TimeoutPubListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *     随机超时时间
 *              最小超时时间 + （最小和最大超时时间间隔 / 集群机器数）* 当前机器编号
 * </p>
 * @author jixinag
 * @date 2022/1/23
 */
@Slf4j
@Getter
public class TimeoutClock {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = ClockThreadPool.THREAD_POOL_EXECUTOR;

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition condition = reentrantLock.newCondition();
    // 是否暂停
    private boolean yield = false;

    // timeout 就是 follower 等待成为候选人的时间
    // 超时时间，用户动态变化重置
    private int timeout;
    // 固定的超时时间的初始值
    private int timeoutValue;

    /**
     *
     * @param timeout getInstance 计算生成
     */
    private TimeoutClock(int timeout){
        this.timeout = timeout;
        this.timeoutValue = timeout;
    }

    private static TimeoutClock timeoutRandom = null;

    public static TimeoutClock getInstance() {
        if (Objects.isNull(timeoutRandom)) {
            int timeout_ = RandomUtil.randomInt(Config.minTimeout,Config.maxTimeout);
            timeoutRandom = new TimeoutClock(timeout_);
        }
        return timeoutRandom;
    }

    /**
     * 开始超时等待,操作将阻塞 timeout 时间
     * 如果超时时间之后没有收到leader 消息，就发起投票，
     * 期间如果收到leader的心跳就重置clock
     */
    public void start() {
        THREAD_POOL_EXECUTOR.execute(() ->{
            reentrantLock.lock();
            try {
                // timeout 会在接收到其它服务器的消息时进行重置
                while (this.timeout > 0) {
                    if (yield) condition.await();
                    // 阻塞一毫秒
                    LockSupport.parkNanos(100_0000L);
                    this.timeout--;
                }

                log.info("{} 实例超时 {} , 发布 TimeoutEvent ",Config.id, timeoutValue);
                TimeoutPubListener.getInstance().publish(new TimeoutEvent());

            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } finally {
                reentrantLock.unlock();
            }
        });
    }

    /**
     * 重启时钟，重启是在超时线程已经结束，需要重新开启执行
     * 超时时候时钟线程执行完毕，后面就需要重启
     */
    public void restart() {
        this.timeoutValue = RandomUtil.randomInt(Config.minTimeout,Config.maxTimeout);
        this.timeout = this.timeoutValue;
        this.yield = false;
        this.start();
    }

    //###############暂停相当于是在超时线程未结束的情况下restart###############

    /**
     * 暂停 并 重置 当前clock
     */
    public void yieldAndReset() {
        this.yield = true;
        this.timeout = this.timeoutValue;
    }

    /**
     * 取消暂停
     */
    public void cancelYield() {
        reentrantLock.lock();
        try {
            this.yield = false;
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }

    }
}
