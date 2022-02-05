package cn.robby.rpc.log_append;

import cn.hutool.core.util.ByteUtil;
import cn.robby.rpc.AbstractRpcCodec;
import cn.robby.common.value_obj.LogEntry;
import cn.robby.common.value_obj.Term;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     日志添加请求实体
 * </p>
 * @author jixinag
 * @date 2022/1/27
 */
@Getter
@Setter
public class LogAppendRequest extends AbstractRpcCodec<LogAppendRequest> {
    // term 领导人的任期

    // 领导人 ID 因此跟随者可以对客户端进行重定向
    // （跟随者根据领导人 ID 把客户端的请求重定向到领导人，比如有时客户端把请求发给了跟随者而不是领导人）
    private int leaderId;
    // 紧邻新日志条目之前的那个日志条目的索引
    private int prevLogIndex;
    // 紧邻新日志条目之前的那个日志条目的任期
    private Term prevLogTerm;
    // 领导人的已知已提交的最高的日志条目的索引
    private int leaderCommit;
    // 需要被保存的日志条目（被当做心跳使用时，则日志条目内容为空；为了提高效率可能一次性发送多个）
    private List<LogEntry> logEntries;

    @Override
    protected int getType() {
        return logAppendReq;
    }

    /**
     * [leaderId,prevLogIndex,prevLogTerm,leaderCommit,logEntries]
     * @param buffer ByteBuf
     */
    @Override
    protected void subSerialize(ByteBuf buffer) {
        buffer.writeBytes(ByteUtil.intToBytes(leaderId, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(prevLogIndex, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(prevLogTerm.getValue(), ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(leaderCommit, ByteOrder.BIG_ENDIAN));
        logEntries.forEach(ele -> buffer.writeBytes(ele.serialize()));
    }

    @Override
    public LogAppendRequest deserialize(ByteBuf byteBuf) {
        LogAppendRequest appendRequest = new LogAppendRequest();
        appendRequest.setTerm(new Term(byteBuf.readInt()));
        appendRequest.setLeaderId(byteBuf.readInt());
        appendRequest.setPrevLogIndex(byteBuf.readInt());
        appendRequest.setPrevLogTerm(new Term(byteBuf.readInt()));
        appendRequest.setLeaderCommit(byteBuf.readInt());
        ArrayList<LogEntry> logEntries = new ArrayList<>();
        while (byteBuf.isReadable()) {
            logEntries.add(LogEntry.deserialize(byteBuf));
        }
        appendRequest.setLogEntries(logEntries);
        return appendRequest;
    }
}
