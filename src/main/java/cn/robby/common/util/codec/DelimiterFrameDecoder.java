package cn.robby.common.util.codec;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     自定义分隔符解码器
 * </p>
 * @author jixinag
 * @date 2022/1/23
 */
public class DelimiterFrameDecoder{
    public static final String NAME = "DelimiterFrameDecoder";
    private static final int maxFrameLength = 1024;
    // 自定义分隔符
    public static String delimiter = "$";

    public static DelimiterBasedFrameDecoder build() {
        // 默认字节数组去掉分隔符
        return new DelimiterBasedFrameDecoder(
                maxFrameLength,
                Unpooled.copiedBuffer(delimiter.getBytes(StandardCharsets.UTF_8)));
    }

}
