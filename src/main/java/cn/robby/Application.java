package cn.robby;

import cn.robby.server.Server;

/**
 * <p>
 *     启动类
 * </p>
 * @author jixinag
 * @date 2022/1/26
 */
public class Application {
    public static void main(String[] args) throws InterruptedException {
        Server server = Server.getInstance();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(server::sourceClose, "ShutdownHook-EvenLoopClose_Thread"));
        server.start();
    }
}
