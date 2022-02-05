package cn.robby.common;

import cn.robby.common.enums.Role;
import cn.robby.common.status.PersistentStatus;
import cn.robby.common.value_obj.Term;
import cn.robby.rpc.AbstractRpcCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *     如果接收到的 RPC 请求或响应中，
 *     任期号`T > currentTerm`，则令 `currentTerm = T`，
 *     并切换为跟随者状态
 * </p>
 * @author jixinag
 * @date 2022/2/4
 */
@Slf4j
public class CommonInboundHandle extends SimpleChannelInboundHandler<AbstractRpcCodec> {
    public static final String NAME = "CommonInboundHandle";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRpcCodec abstractRpcCodec) throws Exception {
        Term term = abstractRpcCodec.getTerm();
        PersistentStatus persistentStatus = RaftInstance.getInstance().getPersistentStatus();

        log.info(" 请求/响应 任期值 : T: {}, currentTerm: {}", term.getValue() ,persistentStatus.getCurrentTerm().getValue());
        if (term.getValue() > persistentStatus.getCurrentTerm().getValue()) {
            persistentStatus.setCurrentTerm(term);
            RaftInstance.getInstance().setRole(Role.Follower);
        }
        ctx.fireChannelRead(abstractRpcCodec);
    }
}
