package cn.robby.common;

import cn.hutool.core.collection.CollUtil;
import cn.robby.client.Client;
import cn.robby.common.enums.Role;
import cn.robby.common.enums.VoteResult;
import cn.robby.common.pub_listener.impl.TimeoutPubListener;
import cn.robby.common.status.PersistentStatus;
import cn.robby.common.status.VolatileStatus;
import cn.robby.common.util.TimeoutClock;
import cn.robby.common.value_obj.LogEntry;
import cn.robby.rpc.vote.VoteRequest;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *     当前raft 实例
 * </p>
 * @author jixinag
 * @date 2022/1/26
 */
@Getter
@Setter
@Slf4j
public class RaftInstance {
    private static RaftInstance instance = null;

    public static RaftInstance getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RaftInstance();
        }
        return instance;
    }

    private final static ExecutorService singleScheduler = Executors.newSingleThreadExecutor((run) -> {
        Thread thread = new Thread(run,"voteCount_thread");
        thread.setDaemon(true);
        return thread;
    });

    private final static ExecutorService voteReqExecutor = Executors.newFixedThreadPool(Config.cluster.size() + 1,(run) -> {
        Thread thread = new Thread(run,"vote_req");
        thread.setDaemon(true);
        return thread;
    });
    // 当前角色状态
    private volatile Role role = Role.getDefault();
    // 当前的获得的票数统计
    private AtomicInteger voteCount = new AtomicInteger(0);
    // 超时时钟
    private final TimeoutClock timeoutClock = TimeoutClock.getInstance();

    // 持久性存储信息
    private PersistentStatus persistentStatus = new PersistentStatus();
    //易失性信息
    private VolatileStatus volatileStatus = new VolatileStatus();

    /**
     * 增加投票数
     */
    public int voteCountIncrement() {
        return voteCount.incrementAndGet();
    }
    /**
     * 开始执行发起选举
     * 由 {@link TimeoutPubListener} 发起
     */
    public void doVote() {
        // 1 转变成候选人
        this.role = Role.Candidate;
        // 2 自增当前的任期号
        this.persistentStatus.getCurrentTerm().increment();
        // 3 给自己投票,先重置票数(因为之前可能发起过投票，重新发起)
        this.voteCount.set(0);
        this.voteCountIncrement();
        // 4 重置选举超时计时器
        timeoutClock.restart();
        // 5 发送请求投票的 RPC 给其他所有服务器
        VoteResult voteResult = voteRpcParallel();
    }
    /**
     * 并行发起投票
     */
    public VoteResult voteRpcParallel() {
        log.info("开始并行发起选举");
        List<Channel> channels = Client.getInstance().getChannels();

        CountDownLatch latch = new CountDownLatch(channels.size());
        channels.forEach(channel -> {
            VoteRequest voteRequest = genVoteRequest();
            latch.countDown();
            voteReqExecutor.execute(() -> channel.writeAndFlush(voteRequest));
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 获取一个 VoteRequest 实体
     * 每次发送都会创建一个对象，避免线程安全问题
     * @return VoteRequest
     */
    private VoteRequest genVoteRequest() {
        VoteRequest voteReq = new VoteRequest();
        voteReq.setTerm(this.persistentStatus.getCurrentTerm());
        voteReq.setCandidateId(Config.id);

        List<LogEntry> logs = this.persistentStatus.getLogEntries();
        if (CollUtil.isEmpty(logs)) {
            voteReq.initLastLogIndex();
            voteReq.initLastLogTerm();
        }else {
            LogEntry logEntry = logs.get(logs.size() - 1);
            voteReq.setLastLogIndex(logEntry.getIndex());
            voteReq.setLastLogTerm(logEntry.getTerm().getValue());
        }
        return voteReq;
    }


}
