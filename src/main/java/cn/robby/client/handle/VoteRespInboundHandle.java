package cn.robby.client.handle;

import cn.robby.common.Config;
import cn.robby.common.RaftInstance;
import cn.robby.common.pub_listener.impl.BecomeLeaderPubListener;
import cn.robby.common.pub_listener.events.BecomeLeaderEvent;
import cn.robby.common.util.codec.RpcCodec;
import cn.robby.rpc.vote.VoteResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *     client 响应入站处理器
 * </p>
 * @author jixinag
 * @date 2022/1/27
 */
@Slf4j
public class VoteRespInboundHandle extends SimpleChannelInboundHandler<VoteResponse> {
    public static final String NAME = "VoteRespInboundHandle";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VoteResponse voteResponse) {
        log.info("收到投票的响应");
        log.info(voteResponse.toString());
        if (voteResponse.isVoteGranted()) {
            // todo
            int voteCount = RaftInstance.getInstance().voteCountIncrement();
            //  todo 计算投票结果，假如网络绝对稳定，注意发起者本身会给自己投一票
            //voteCount.get() < ((Config.cluster.size() +1) >> 1) + 1)
            // 当票数达到指定的数量发布成为leader 的事件
            // 目前指定所有票数才算通过
            if (voteCount == (Config.cluster.size() +1)) {
                BecomeLeaderPubListener.getInstance().publish(new BecomeLeaderEvent());
            }
        }
        ctx.fireChannelRead(voteResponse);
    }
}
