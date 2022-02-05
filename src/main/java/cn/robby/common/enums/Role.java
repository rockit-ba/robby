package cn.robby.common.enums;

/**
 * <p>
 * 实例的角色
 * </p>
 *
 * @author jixinag
 * @date 2022/1/20
 */
public enum Role {
    Leader,
    Follower,
    Candidate;

    /**
     * 默认都是 Follower
     *
     * @return Follower
     */
    public static Role getDefault() {
        return Follower;
    }

}