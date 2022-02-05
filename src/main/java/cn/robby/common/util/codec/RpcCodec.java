package cn.robby.common.util.codec;

import io.netty.buffer.ByteBuf;

/**
 * <p>
 *     网络对象传输统计编解码接口
 * </p>
 * @author jixinag
 * @date 2022/1/26
 */
public interface RpcCodec<T>{
    // 类型标记
    int voteReq = 1;
    int voteResp = 2;
    int logAppendReq = 3;
    int logAppendResp = 4;

    /**
     * 序列化
     * @return byte[]
     */
    byte[] serialize();

    /**
     * 反序列化
     * @param byteBuf ByteBuf
     * @return Object
     */
    Object deserialize(ByteBuf byteBuf);

}
