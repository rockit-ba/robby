package cn.robby.rpc.vote;

import cn.hutool.core.util.ByteUtil;
import cn.robby.rpc.AbstractRpcCodec;
import cn.robby.common.value_obj.Term;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteOrder;

/**
 * <p>
 *     投票请求实体
 * </p>
 * @author jixinag
 * @date 2022/1/27
 */
@Getter
@Setter
public class VoteRequest extends AbstractRpcCodec {
    // term候选人的任期号
    //请求选票的候选人的 ID
    private int candidateId;
    // 候选人的最后日志条目的索引值
    private int lastLogIndex;
    // 候选人最后日志条目的任期号
    private int lastLogTerm;

    /**
     * 设置初始 的 lastLogIndex 值
     */
    public void initLastLogIndex() {
        this.lastLogIndex = 0;
    }
    /**
     * 设置初始 的 lastLogTerm 值
     */
    public void initLastLogTerm() {
        this.lastLogTerm = 0;
    }


    @Override
    protected void subSerialize(ByteBuf buffer) {
        buffer.writeBytes(ByteUtil.intToBytes(this.candidateId, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(this.lastLogIndex, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(this.lastLogTerm, ByteOrder.BIG_ENDIAN));
    }

    @Override
    protected int getType() {
        return voteReq;
    }

    /**
     * 发序列化 VoteRequest
     * @param byteBuf byteBuf
     * @return VoteRequest
     */
    @Override
    public VoteRequest deserialize(ByteBuf byteBuf) {
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setTerm(new Term(byteBuf.readInt()));
        voteRequest.setCandidateId(byteBuf.readInt());
        voteRequest.setLastLogIndex(byteBuf.readInt());
        voteRequest.setLastLogTerm(byteBuf.readInt());
        return voteRequest;
    }

    @Override
    public String toString() {
        return "VoteRequest{" +
                ", candidateId=" + candidateId +
                ", lastLogIndex=" + lastLogIndex +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }
}
