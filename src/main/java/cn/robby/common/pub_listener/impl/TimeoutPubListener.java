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
 *     超时事件发布订阅implement
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
                // 只有当前的角色是 Follower 才可以发起 投票
                if (instance.getRole().equals(Role.Follower)) {
                    instance.doVote();
                }
            };
            timeoutPubListener = new TimeoutPubListener();
            timeoutPubListener.listener(consumer, "timeout_pubListener_thread");
        }
        return timeoutPubListener;
    }
}
