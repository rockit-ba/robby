package cn.robby.common.value_obj;

import cn.hutool.core.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     条目包含了用于状态机的命令，以及领导人接收到该条目时的任期（初始索引为1）
 * </p>
 * @author jixinag
 * @date 2022/1/25
 */
@Getter
@Setter
public class LogEntry {
    private int index;
    private Term term;
    private String command;

    public LogEntry() {}

    public LogEntry(String command, int index, Term term) {
        this.command = command;
        this.index = index;
        this.term = term;
    }

    /**
     * 序列化 [index,term,command_len,command]
     * @return byte[]
     */
    public byte[] serialize() {
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(ByteUtil.intToBytes(index, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(ByteUtil.intToBytes(term.getValue(), ByteOrder.BIG_ENDIAN));
        byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);
        // 命令的长度
        buffer.writeBytes(ByteUtil.intToBytes(commandBytes.length, ByteOrder.BIG_ENDIAN));
        buffer.writeBytes(commandBytes);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        buffer.release();
        return bytes;
    }

    /**
     * 反序列化单个 LogEntry
     * @param buf ByteBuf
     * @return LogEntry
     */
    public static LogEntry deserialize(ByteBuf buf) {
        LogEntry logEntry = new LogEntry();
        logEntry.setIndex(buf.readInt());
        logEntry.setTerm(new Term(buf.readInt()));
        int commandLen = buf.readInt();
        CharSequence command = buf.readCharSequence(commandLen, StandardCharsets.UTF_8);
        logEntry.setCommand(command.toString());
        return logEntry;
    }
}
