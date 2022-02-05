package cn.robby;

import cn.robby.client.ClientChannelInitializer;
import cn.robby.common.value_obj.Term;
import cn.robby.rpc.vote.VoteRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.locks.LockSupport;

public class ClientTest {
    public static final String host = "localhost";
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer());

            Channel channel = bootstrap.connect(host, 9001).sync().channel();
            VoteRequest voteReq = new VoteRequest();
            voteReq.setTerm(new Term());
            voteReq.setCandidateId(0);
            voteReq.setLastLogIndex(0);
            voteReq.setLastLogTerm(0);
            channel.writeAndFlush(voteReq);
            LockSupport.parkNanos(10000000000_0000L);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }


    }
}
