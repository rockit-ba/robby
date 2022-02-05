package cn.robby.common;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>
 *     ip:port
 * </p>
 * @author jixinag
 * @date 2022/1/21
 */
@Getter
@Slf4j
public class Address implements Serializable {
    private final String host;
    private final Integer port;

    public Address(String content) {
        if (StrUtil.isBlank(content)) {
            String error_ = "content 不能为空";
            log.error(error_, new RuntimeException(error_));
        }
        String[] split = content.trim().split(":");
        if (split.length != 2) {
            String error_ = "content 非法";
            log.error(error_, new RuntimeException(error_));
        }
        this.host = split[0];
        this.port = Integer.valueOf(split[1]);
    }

    @Override
    public String toString() {
        return String.format("%s:%s",host,port);
    }
}
