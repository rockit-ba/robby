package cn.robby.common.util.codec;

import cn.hutool.core.util.ByteUtil;

import java.nio.ByteOrder;

/**
 * <p>
 *     布尔值序列化，转化为 1/0 的字节序
 * </p>
 * @author jixinag
 * @date 2022/1/26
 */
public class BooleanCodec {
    public static byte[] Serialize(Boolean value) {
        if (value) {
            return ByteUtil.intToBytes(1, ByteOrder.BIG_ENDIAN);
        }
        return ByteUtil.intToBytes(0, ByteOrder.BIG_ENDIAN);
    }

    public static Boolean deserialize(byte[] bytes) {
        int anInt = ByteUtil.bytesToInt(bytes, ByteOrder.BIG_ENDIAN);
        return anInt != 0;
    }

    public static Boolean deserialize(int value) {
        return value != 0;
    }

}
