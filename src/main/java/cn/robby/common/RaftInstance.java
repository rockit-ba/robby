package cn.robby.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.robby.client.Client;
import cn.robby.common.enums.Role;
import cn.robby.common.enums.VoteResult;
import cn.robby.common.status.PersistentStatus;
import cn.robby.common.status.VolatileStatus;
import cn.robby.common.util.TimeoutClock;
import cn.robby.common.value_obj.LogEntry;
import cn.robby.common.value_obj.Term;
import cn.robby.rpc.vote.VoteRequest;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private final static Scheduler singleScheduler = Schedulers.newSingle("voteCount_thread");
    // 对应配置文件的 id
    private final int id = Config.id;
    // 当前角色状态
    private volatile Role role = Role.getDefault();
    // 当前的获得的票数统计
    private AtomicInteger voteCount = new AtomicInteger(0);
    // 是否已经投过票了
    private AtomicBoolean voted = new AtomicBoolean(false);
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
     * 发起选举
     */
    public void vote() {
        // 1 转变成候选人
        this.role = Role.Candidate;
        // 2 自增当前的任期号
        this.persistentStatus.getCurrentTerm().increment();
        // 3 给自己投票,先重置票数
        this.voteCount.set(0);
        this.voteCountIncrement();
        this.voted.set(true);
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
            channel.writeAndFlush(genVoteRequest());
            latch.countDown();
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
     * 该对象避免共享操作
     * @return VoteRequest
     */
    private VoteRequest genVoteRequest() {
        VoteRequest voteReq = new VoteRequest();
        voteReq.setTerm(this.persistentStatus.getCurrentTerm());
        voteReq.setCandidateId(this.id);

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
