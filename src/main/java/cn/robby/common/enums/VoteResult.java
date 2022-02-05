package cn.robby.common.enums;

/**
 * <p>
 *     发起投票的结果
 * </p>
 * @author jixinag
 * @date 2022/1/24
 */
public enum VoteResult {
    // 赢得了选举
    Wined,
    // 输了选举
    Defeated,
    // 没赢也没输，因此需要重新发起新一轮的投票
    Draw;
}
