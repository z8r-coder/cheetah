package rpc;

import constants.Globle;
import org.apache.log4j.Logger;
import rpc.client.SimpleClientRemoteProxy;
import rpc.demo.rpc.provider.HelloRpcService;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc
 */
public class RpcSingleNodeMutilThreadDemo {

    private static final Logger logger = Logger.getLogger(RpcSingleNodeMutilThreadDemo.class);

    public static void main(String[] args) {
        RpcServerAcceptor acceptor = new RpcServerAcceptor(Globle.localHost, Globle.localPortTest1);
        acceptor.startService();
        RpcServerSyncConnector connector = new RpcServerSyncConnector(Globle.localHost, Globle.localPortTest1);
        connector.startService();
        SimpleClientRemoteProxy proxy = connector.getProxy();
        HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
        System.out.println(helloRpcService.getHello());
    }
}
