package cn.robby.client;

import cn.robby.client.handle.VoteRespInboundHandle;
import cn.robby.common.CommonInboundHandle;
import cn.robby.common.util.codec.DelimiterFrameDecoder;
import cn.robby.common.util.codec.RpcCodecDecoder;
import cn.robby.common.util.codec.RpcCodecEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * <p>
 *     ChannelInitializer
 *     resp 是相对于处理器处理的请求的类型来说的
 * </p>
 * @author jixinag
 * @date 2022/1/21
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(DelimiterFrameDecoder.NAME, DelimiterFrameDecoder.build());
        pipeline.addLast(RpcCodecDecoder.NAME, new RpcCodecDecoder());
        pipeline.addLast(RpcCodecEncoder.NAME, new RpcCodecEncoder());
        addSpecHandle(pipeline);

    }

    /**
     * 添加业务处理handle
     * @param pipeline ChannelPipeline
     */
    private void addSpecHandle(ChannelPipeline pipeline) {
        pipeline.addLast(CommonInboundHandle.NAME, new CommonInboundHandle());
        pipeline.addLast(VoteRespInboundHandle.NAME, new VoteRespInboundHandle());

    }

}
