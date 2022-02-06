package cn.robby.common.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *   clock 超时处理线程池
 * </p>
 * @author jixinag
 * @date 2022/1/28
 */
public class ClockThreadPool {
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    static {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 1,
                4, TimeUnit.HOURS,
                new LinkedBlockingQueue<>(16), r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("clock_thread" + atomicInteger.incrementAndGet());
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
    }
}









