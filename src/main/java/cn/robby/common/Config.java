package cn.robby.common;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <p>
 *     配置类
 * </p>
 * @author jixinag
 * @date 2022/1/20
 */
@Getter
@Slf4j
public class Config {
    public static final String serverHost;
    // 实例启动端口; 超时时间可选range
    public static final Integer serverPort,
            minTimeout, maxTimeout,
            // 当前服务器的编号id
            id;
    // 集群地址
    public static final List<Address> cluster;

    static {
        Properties properties = new Properties();
        log.info("配置init：");
        try {
            FileInputStream inputStream = new FileInputStream(ResourceUtil.getResource("config.properties").getFile()) ;
            properties.load(inputStream);
        }catch (IOException exception) {
            log.error("加载 config.properties 失败", exception);
        }

        properties.forEach((key, val) -> {
            log.info(String.format("[ %s -> %s ]",key, val));
        });

        serverPort = Integer.valueOf((String) properties.get("server.port"));
        minTimeout = Integer.valueOf((String) properties.get("min.timeout"));
        maxTimeout = Integer.valueOf((String) properties.get("max.timeout"));
        serverHost = (String) properties.get("server.host");
        id = Integer.valueOf((String) properties.get("id"));


        String cluster_str_ = (String) properties.get("cluster");
        cluster = Arrays.stream(cluster_str_.replaceAll("[\\[\\]]", "")
                .trim().split(","))
                .map(Address::new).collect(Collectors.toList());

    }

}
