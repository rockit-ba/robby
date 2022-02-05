package cn.robby.rpc;

import cn.hutool.core.util.ByteUtil;
import cn.robby.common.util.codec.RpcCodec;
import cn.robby.common.value_obj.Term;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.ByteOrder;
import java.util.Objects;

@Getter
@Setter
public abstract class AbstractRpcCodec<T> implements RpcCodec<T>, Serializable {
    private Term term;

    // 并发操作此buffer 的时候内部的数据会有线程安全问题
    // 目前采用了多个实例的方式避免改问题
    @Override
    public byte[] serialize() {
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        // 没和序列化都要的字节： type 和 term
        buffer.writeBytes(ByteUtil.intToBytes(getType(), ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(this.getTerm().getValue(), ByteOrder.BIG_ENDIAN));
        subSerialize(buffer);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        buffer.clear();
        buffer.release();
        return bytes;
    }

    protected abstract int getType();

    protected abstract void subSerialize(ByteBuf buffer);
}
