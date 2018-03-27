package rpc.demo.rpc.async;

import org.apache.log4j.Logger;
import rpc.async.RpcCallback;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.demo.rpc.provider.HelloRpcService;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc 异步调用客户端测试，服务端用以前的
 */
public class RpcAsyncClient {

    private static final Logger logger = Logger.getLogger(RpcAsyncClient.class);

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 4332;
        AbstractRpcConnector connector = new RpcNioConnector(null);
        connector.setHost(host);
        connector.setPort(port);

        RpcCallback<String> callback = new RpcCallback<String>() {
            public void success(String resp) {
                System.out.println("this is async " + resp);
            }

            public void fail(Throwable t) {
                System.out.println(t);
            }
        };

        RpcCallback<String> testCallBack = new TestCallBack();

        AsyncClientRemoteExecutor remoteExecutor = new AsyncClientRemoteExecutor(connector, testCallBack);
        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(remoteExecutor);
        proxy.startService();

        HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
        logger.info("start client");

        helloRpcService.getHello();
    }
}
