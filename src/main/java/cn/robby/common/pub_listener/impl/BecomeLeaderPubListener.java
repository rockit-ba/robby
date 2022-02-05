package cn.robby.common.pub_listener.impl;

import cn.robby.common.Config;
import cn.robby.common.RaftInstance;
import cn.robby.common.enums.Role;
import cn.robby.common.pub_listener.PubListener;
import cn.robby.common.pub_listener.events.BecomeLeaderEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * <p>
 *     发起选举成功leader 监听
 * </p>
 * @author jixinag
 * @date 2022/2/4
 */
@Slf4j
public class BecomeLeaderPubListener extends PubListener<BecomeLeaderEvent> {
    private BecomeLeaderPubListener() {}
    private static BecomeLeaderPubListener listener = null;

    public static BecomeLeaderPubListener getInstance() {
        if (Objects.isNull(listener)) {
            Consumer<BecomeLeaderEvent> consumer = (event) -> {
                RaftInstance instance = RaftInstance.getInstance();
                instance.getTimeoutClock().yieldAndReset();
                instance.setRole(Role.Leader);
                log.info("{} 成为了leader", Config.id);
                // TODO 成为leader之后发送空的 append_log 压制其它服务器超时
            };
            listener = new BecomeLeaderPubListener();
            listener.listener(consumer, "becomeLeader_pubListener_thread");
        }
        return listener;
    }
}
