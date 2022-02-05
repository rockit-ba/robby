package cn.robby.server.handle;

import cn.robby.common.RaftInstance;
import cn.robby.rpc.vote.VoteRequest;
import cn.robby.rpc.vote.VoteResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * <p>
 *     server 请求入站处理器
 *     处理器中操作的数据都会有线程安全的问题
 *     处理 投票的请求
 *     如果`term < currentTerm`返回 false
 *     如果 votedFor 为空或者为 candidateId，并且候选人的日志至少和自己一样新，那么就投票给他
 * </p>
 * @author jixinag
 * @date 2022/1/27
 */
@Slf4j
public class VoteReqInboundHandle extends SimpleChannelInboundHandler<VoteRequest> {
    public static final String NAME = "ServerReqInboundHandle";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VoteRequest voteRequest) {
        log.info("收到投票请求");
        RaftInstance instance = RaftInstance.getInstance();
        // todo
        Supplier<Boolean> voteFlag = () -> {
            AtomicInteger votedFor = instance.getPersistentStatus().getVotedFor();
            return votedFor.get() < 0 || votedFor.get() == voteRequest.getCandidateId();
        };

        // 符合条件才可以投票
        if (voteFlag.get()) {
            VoteResponse voteResponse = new VoteResponse();
            voteResponse.vote();
            ctx.channel().writeAndFlush(voteResponse);
            instance.getPersistentStatus()
                    .getVotedFor().set(voteRequest.getTerm().getValue());
        }
        ctx.fireChannelRead(voteRequest);
    }
}
