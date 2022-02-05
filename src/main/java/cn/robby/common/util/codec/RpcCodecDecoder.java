package cn.robby.common.util.codec;

import cn.robby.common.util.TimeoutClock;
import cn.robby.rpc.log_append.LogAppendRequest;
import cn.robby.rpc.log_append.LogAppendResponse;
import cn.robby.rpc.vote.VoteRequest;
import cn.robby.rpc.vote.VoteResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *     RpcCodec 对象解码器
 *     RpcCodec To Object
 * </p>
 * @author jixinag
 * @date 2022/1/23
 */
@Slf4j
public class RpcCodecDecoder extends ByteToMessageDecoder {
    public static final String NAME = "RpcCodecDecoder";
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 请求的类型
        int type = in.readInt();
        RpcCodec rpcCodec = null;
        switch (type) {
            case RpcCodec.voteReq:
                rpcCodec = new VoteRequest();
                break;
            case RpcCodec.voteResp:
                rpcCodec = new VoteResponse();
                break;
            case RpcCodec.logAppendReq:
                rpcCodec = new LogAppendRequest();
                break;
            case RpcCodec.logAppendResp:
                rpcCodec = new LogAppendResponse();
                break;
        }
        Objects.requireNonNull(rpcCodec, "Rpc type 解析失败！");
        out.add(rpcCodec.deserialize(in));
    }
}
