package cn.robby.server;

import cn.robby.common.CommonInboundHandle;
import cn.robby.common.RaftInstance;
import cn.robby.common.util.codec.DelimiterFrameDecoder;
import cn.robby.common.util.codec.RpcCodecDecoder;
import cn.robby.common.util.codec.RpcCodecEncoder;
import cn.robby.server.handle.VoteReqInboundHandle;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(CommonPreInboundHandle.NAME, new CommonPreInboundHandle());

        pipeline.addLast(DelimiterFrameDecoder.NAME, DelimiterFrameDecoder.build());
        pipeline.addLast(RpcCodecDecoder.NAME, new RpcCodecDecoder());
        pipeline.addLast(RpcCodecEncoder.NAME, new RpcCodecEncoder());
        addSpecHandle(pipeline);
        //pipeline.addLast(CancelYieldClockInboundHandle.NAME, new CancelYieldClockInboundHandle());

    }

    /**
     * 添加业务处理handle
     * @param pipeline ChannelPipeline
     */
    private void addSpecHandle(ChannelPipeline pipeline) {
        pipeline.addLast(CommonInboundHandle.NAME, new CommonInboundHandle());
        pipeline.addLast(VoteReqInboundHandle.NAME, new VoteReqInboundHandle());

    }

    /**
     * server 入账处理器的第一个处理器，负责暂停超时时钟，避免处理过程中自己超时
     */
    static class CommonPreInboundHandle extends ChannelInboundHandlerAdapter {
        public static final String NAME = "CommonPreInboundHandle";
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("收到其它服务器的请求，开始暂停clock");
            RaftInstance.getInstance().getTimeoutClock().yieldAndReset();
            super.channelRead(ctx, msg);
        }
    }


    /**
     * server 入账处理器的最后一个handle。负责恢复超时时钟，恢复后的时钟时间已经被 YieldClockInboundHandle 重置
     */
    static class CancelYieldClockInboundHandle extends ChannelInboundHandlerAdapter {
        public static final String NAME = "CancelYieldClockInboundHandle";
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("收到其它服务器的请求，处理完毕，开始恢复clock");
            RaftInstance.getInstance().getTimeoutClock().cancelYield();
            super.channelRead(ctx, msg);
        }
    }
}
