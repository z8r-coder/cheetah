package rpc.demo.rpc.nio;

import org.apache.log4j.Logger;
import rpc.demo.rpc.provider.HelloRpcService;
import rpc.demo.rpc.provider.impl.HelloRpcServiceImpl;
import rpc.net.AbstractRpcAcceptor;
import rpc.nio.RpcNioAcceptor;
import rpc.server.RpcServiceProvider;
import rpc.server.SimpleServerRemoteExecutor;

/**
 * @Author:Roy
 * @Date: Created in 17:17 2017/12/3 0003
 */
public class RpcNioServer {

    private static Logger logger = 	Logger.getLogger(RpcNioServer.class);

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 4332;

        AbstractRpcAcceptor acceptor = new RpcNioAcceptor();
        acceptor.setHost(host);
        acceptor.setPort(port);
        RpcServiceProvider provider = new RpcServiceProvider();

        SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();

        HelloRpcServiceImpl obj = new HelloRpcServiceImpl();

        proxy.registerRemote(HelloRpcService.class, obj);

        HelloRpcServiceImpl obj2 = new HelloRpcServiceImpl();

        proxy.registerRemote(HelloRpcService.class, obj2);
        

        provider.setExecutor(proxy);

//		provider.addRpcFilter(new MyTestRpcFilter());
//
//		provider.addRpcFilter(new RpcLoginCheckFilter());

        acceptor.addRpcCallListener(provider);

        acceptor.startService();

        logger.info("service started");
    }
}
