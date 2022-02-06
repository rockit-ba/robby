package cn.robby.common.util.codec;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     自定义分隔符解码器，组合了 {@link DelimiterBasedFrameDecoder}
 * </p>
 * @author jixinag
 * @date 2022/1/23
 */
public class DelimiterFrameDecoder{
    public static final String NAME = "DelimiterFrameDecoder";
    private static final int maxFrameLength = Integer.MAX_VALUE;
    // 自定义分隔符
    public static String delimiter = "$";

    public static DelimiterBasedFrameDecoder build() {
        // 默认分隔后的字节包去掉 delimiter 字节
        return new DelimiterBasedFrameDecoder(
                maxFrameLength,
                Unpooled.copiedBuffer(delimiter.getBytes(StandardCharsets.UTF_8)));
    }

}
