package rpc;

import rpc.async.RpcCallback;
import rpc.client.AbstractClientRemoteExecutor;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.demo.rpc.provider.HelloRpcService;
import rpc.wrapper.RpcConnectorWrapper;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc
 */
public class RpcServerAsyncConnector extends RpcConnectorWrapper {

    public RpcServerAsyncConnector(String host, int port) {
        super(host, port);
    }

    @Override
    public AbstractClientRemoteExecutor getClientRemoteExecutor() {
        RpcTestCallBack rpcTestCallBack = new RpcTestCallBack();
        AsyncClientRemoteExecutor remoteExecutor = new AsyncClientRemoteExecutor(connector, rpcTestCallBack);
        return remoteExecutor;
    }

    public class RpcTestCallBack implements RpcCallback<String> {

        @Override
        public void success(String resp) {
            System.out.println("rpc async call success!");
            System.out.println(resp);
        }

        @Override
        public void fail(Throwable t) {
            System.out.println("rpc async call fail!");
        }
    }
}
