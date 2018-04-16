package rpc;

import constants.Globle;
import rpc.demo.rpc.provider.HelloRpcService;
import rpc.demo.rpc.provider.impl.HelloRpcServiceImpl;
import rpc.wrapper.RpcAcceptorWrapper;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc rpc 单点多线程
 */
public class RpcServerAcceptor extends RpcAcceptorWrapper {

    public RpcServerAcceptor(String host, int port) {
        super(host, port);
    }

    @Override
    public void register() {
        HelloRpcServiceImpl helloRpcService = new HelloRpcServiceImpl();
        remoteExecutor.registerRemote(HelloRpcService.class, helloRpcService);
    }

    public static void main(String[] args) {
        RpcServerAcceptor acceptor = new RpcServerAcceptor(Globle.localHost, Globle.localPortTest1);
        acceptor.startService();
    }
}
