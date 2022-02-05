package cn.robby.common.pub_listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

/**
 * 所有事件监听的基类
 * @param <E>
 */
public class PubListener<E> {
    private final ArrayBlockingQueue<E> blockingQueue = new ArrayBlockingQueue<>(8);

    /**
     * 开始监听
     * @param consumer 实现类自定义事件的消费方法
     * @param name 线程名字
     */
    protected void listener(Consumer<E> consumer, String name) {
        new Thread(() -> {
            E event;
            try {
                while (true) {
                    event = blockingQueue.take();
                    consumer.accept(event);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, name).start();
    }

    /**
     * 发布对应的事件
     * @param event E
     */
    public void publish(E event) {
        try {
            blockingQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
