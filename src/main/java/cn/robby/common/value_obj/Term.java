package cn.robby.common.value_obj;

import lombok.Getter;

/**
 * <p>
 *     任期号
 * </p>
 * @author jixinag
 * @date 2022/1/25
 */
@Getter
public class Term {
    private int value = 0;

    public Term() {
    }

    public Term(int value) {
        this.value = value;
    }

    /**
     * 对当前的term 编号进行增/减，正数+；负数-；
     * @param offset int
     */
    public void change(int offset) {
        this.value += offset;
    }

    /**
     * 自增
     */
    public void increment() {
        this.value++;
    }

    @Override
    public String toString() {
        return "Term{" +
                "value=" + value +
                '}';
    }
}
