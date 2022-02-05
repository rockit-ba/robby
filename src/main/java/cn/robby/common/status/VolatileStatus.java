package cn.robby.common.status;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * <p>
 *     所有服务器上的易失性状态
 * </p>
 * @author jixinag
 * @date 2022/1/25
 */
@Getter
@Setter
public class VolatileStatus {
    // 已知已提交的最高的日志条目的索引（初始值为0，单调递增）
    private int commitIndex = 0;
    // 已经被应用到状态机的最高的日志条目的索引（初始值为0，单调递增）
    private int lastApplied = 0;

    //#####################领导人（服务器）上的易失性状态(选举后已经重新初始化)###################

    // 对于每一台服务器，发送到该服务器的下一个日志条目的索引（初始值为领导人最后的日志条目的索引+1）
    private HashMap<Integer,Integer> nextIndexMap = new HashMap<>();
    //对于每一台服务器，已知的已经复制到该服务器的最高日志条目的索引（初始值为0，单调递增）
    private HashMap<Integer,Integer> matchIndexMap = new HashMap<>();

}
