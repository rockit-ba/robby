package cn.robby.server;

import cn.robby.client.Client;
import cn.robby.common.Config;
import cn.robby.common.RaftInstance;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class Server {
    private final int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private static Server server = null;

    private Server() {
        if (Objects.isNull(Config.serverPort)) {
            String error_ = "serverPort 不存在！";
            log.error(error_, new RuntimeException(error_));
        }
        this.port = Config.serverPort;
    }

    public static Server getInstance() {
        if (Objects.isNull(server)) {
            server = new Server();
        }
        return server;
    }

    /**
     * 关闭线程组资源
     */
    public void sourceClose() {
        // 这里关闭 client 资源
        Client.sourceClose();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("服务已关闭，资源释放");
    }

    /**
     * 启动 netty
     */
    public void start() throws InterruptedException {
        log.info("Kobby starting ..........");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(port).sync();
        future.channel().closeFuture().addListener((ele) -> {
            log.info("服务通道关闭。");
        });
        log.info("Kobby start ，port - > [{}]", port);

        // 客户端channels 初始化,server 启动之后就会接受其他服务器的信息，因此不能将client的初始化
        // 绑定到raft_instance上面，会导致多次初始化
        Client.getInstance();
        // 初始化 当前raft 实例
        doStartAfter(RaftInstance.getInstance());
    }

    /**
     * server 和 client 初始化之后执行
     * @param instance RaftInstance
     */
    private void doStartAfter(RaftInstance instance) {
        log.info("{} 实例 doStartAfter", Config.id);
        instance.getTimeoutClock().start();

    }
}
