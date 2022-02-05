package cn.robby.common.pub_listener.impl;

import cn.robby.common.RaftInstance;
import cn.robby.common.enums.Role;
import cn.robby.common.pub_listener.PubListener;
import cn.robby.common.pub_listener.events.TimeoutEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 *     超时事件发布订阅
 * </p>
 * @author jixinag
 * @date 2022/1/29
 */
@Slf4j
public class TimeoutPubListener extends PubListener<TimeoutEvent> {
    private TimeoutPubListener() {}
    private static TimeoutPubListener timeoutPubListener = null;

    /**
     * 获取一盒listener并开始监听
     * @return TimeoutPubListener
     */
    public static TimeoutPubListener getInstance() {
        if (Objects.isNull(timeoutPubListener)) {
            Consumer<TimeoutEvent> consumer = (event) -> {
                RaftInstance instance = RaftInstance.getInstance();
                // 如果当前的实例已经成为leader则忽略后续的投票事件
                if (instance.getRole().equals(Role.Follower)) {
                    instance.vote();
                }
            };
            timeoutPubListener = new TimeoutPubListener();
            timeoutPubListener.listener(consumer, "timeout_pubListener_thread");
        }
        return timeoutPubListener;
    }
}
