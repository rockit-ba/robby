package cn.robby.rpc.vote;

import cn.robby.common.RaftInstance;
import cn.robby.rpc.AbstractRpcCodec;
import cn.robby.common.util.codec.BooleanCodec;
import cn.robby.common.value_obj.Term;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *     投票响应
 * </p>
 * @author jixinag
 * @date 2022/1/26
 */
@Getter
@Setter
public class VoteResponse extends AbstractRpcCodec<VoteResponse> {
    // term当前任期号，以便于候选人去更新自己的任期号
    // 候选人赢得了此张选票时为真
    private boolean voteGranted;

    // 此方法不会手动调用，只会被父类中调用
    @Override
    protected void subSerialize(ByteBuf buffer) {
        buffer.writeBytes(BooleanCodec.Serialize(voteGranted));
    }

    @Override
    public VoteResponse deserialize(ByteBuf byteBuf) {
        VoteResponse voteResponse = new VoteResponse();
        voteResponse.setTerm(new Term(byteBuf.readInt()));
        voteResponse.setVoteGranted(BooleanCodec.deserialize(byteBuf.readInt()));
        return voteResponse;
    }

    @Override
    protected int getType() {
        return voteResp;
    }

    /**
     * 投赞成票
     */
    public void vote() {
        this.setVoteGranted(true);
        this.setTerm(RaftInstance.getInstance().getPersistentStatus().getCurrentTerm());
    }

    @Override
    public String toString() {
        return "VoteResponse{" +
                "voteGranted=" + voteGranted +
                '}';
    }
}
