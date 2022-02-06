package cn.robby.common.status;

import cn.robby.common.value_obj.LogEntry;
import cn.robby.common.value_obj.Term;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *     所有服务器上的持久性状态
 *     (在响应 RPC 请求之前，已经更新到了稳定的存储设备)
 * </p>
 * @author jixinag
 * @date 2022/1/25
 */
@Getter
@Setter
public class PersistentStatus {
    // 服务器已知最新的任期（在服务器首次启动时初始化为0，单调递增）
    private Term currentTerm = new Term();

    /**
     * 当前任期内收到选票的 candidateId，如果没有投给任何候选人 则为负数
     * 由于会在handle中并发更新 {@link cn.robby.server.handle.VoteReqInboundHandle}，使用原子类
     */
    private AtomicInteger votedFor = new AtomicInteger(-1);
    //日志条目
    private List<LogEntry> logEntries = new ArrayList<>();

}
