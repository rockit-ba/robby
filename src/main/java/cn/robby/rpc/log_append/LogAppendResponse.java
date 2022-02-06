package cn.robby.rpc.log_append;

import cn.robby.common.util.codec.BooleanCodec;
import cn.robby.common.value_obj.Term;
import cn.robby.rpc.AbstractRpcCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogAppendResponse extends AbstractRpcCodec {
    // term当前任期，对于领导人而言 它会更新自己的任期
    // 如果跟随者所含有的条目和 prevLogIndex 以及 prevLogTerm 匹配上了，则为 true
    private boolean success;

    @Override
    protected void subSerialize(ByteBuf buffer) {
        buffer.writeBytes(BooleanCodec.Serialize(success));
    }

    @Override
    public LogAppendResponse deserialize(ByteBuf byteBuf) {
        LogAppendResponse appendResp = new LogAppendResponse();
        appendResp.setTerm(new Term(byteBuf.readInt()));
        appendResp.setSuccess(BooleanCodec.deserialize(byteBuf.readInt()));
        return appendResp;
    }

    @Override
    protected int getType() {
        return logAppendResp;
    }
}
