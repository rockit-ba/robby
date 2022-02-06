package cn.robby.common.value_obj;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *     任期号
 * </p>
 * @author jixinag
 * @date 2022/1/25
 */
public class Term {
    // 初始为0
    private AtomicInteger value = new AtomicInteger(0);

    public Term() {
    }

    public Term(int value) {
        this.value = new AtomicInteger(value);
    }

    /**
     * 自增
     */
    public void increment() {
        this.value.incrementAndGet();
    }

    public int getValue() {
        return this.value.get();
    }


    @Override
    public String toString() {
        return "Term{" +
                "value=" + value.get() +
                '}';
    }
}
