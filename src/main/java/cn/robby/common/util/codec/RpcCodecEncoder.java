package cn.robby.common.util.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 *     RpcCodec 对象编码器
 *     RpcCodec To Byte
 * </p>
 * @author jixinag
 * @date 2022/1/23
 */
public class RpcCodecEncoder extends MessageToByteEncoder<RpcCodec> {
    public static final String NAME = "RpcCodecEncoder";

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcCodec msg, ByteBuf out) {
        if (Objects.isNull(msg)) {
            return;
        }
        out.writeBytes(msg.serialize());
        // 写入分隔符
        out.writeBytes(DelimiterFrameDecoder.delimiter.getBytes(StandardCharsets.UTF_8));
    }
}
