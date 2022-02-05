package cn.robby.client;

import cn.robby.common.Address;
import cn.robby.common.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * 集群内客户端，用于发送 Rpc请求
 * </p>
 *
 * @author jixinag
 * @date 2022/1/20
 */
@Slf4j
@Getter
public class Client {
    // 集群中其它机器的 channel
    private final List<Channel> channels = new ArrayList<>();
    // 资源
    private final static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private static Client client = null;

    public static Client getInstance() {
        if (Objects.isNull(client)) {
            client = new Client();
        }
        return client;
    }

    private Client() {
        log.info("client channel init .........");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer());

        // 存放已经成功的标记
        HashSet<String> tempSet = new HashSet<>();
        Flux.fromArray(Config.cluster.toArray(new Address[0]))
                .<Optional<Channel>>map(address -> {
                    if (tempSet.contains(address.toString())) {
                        // 如果已经成功直接返回，不进行retry
                        return Optional.empty();
                    }
                    LockSupport.parkNanos(3000_00_0000L);
                    log.info("{} 连接中....", address);

                    try {
                        Channel channel = bootstrap.connect(address.getHost(), address.getPort()).sync().channel();
                        tempSet.add(address.toString());
                        return Optional.of(channel);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    return Optional.empty();
                })
                .retry()
                .subscribe(ele -> {
                    ele.ifPresent(channel -> {
                        channels.add(channel);
                        log.info("连接成功：{}", channel.remoteAddress());
                    });
                });

        log.info("client channels 初始化成功。");
    }

    /**
     * 关闭资源
     */
    public static void sourceClose() {
        eventLoopGroup.shutdownGracefully();
        log.info("client source close");
    }

}
