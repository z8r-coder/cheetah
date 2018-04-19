package rpc.wrapper.connector;

import rpc.async.RpcCallback;
import rpc.client.AbstractClientRemoteExecutor;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.wrapper.RpcConnectorWrapper;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc
 */
public class RpcServerAsyncConnector extends RpcConnectorWrapper {

    private RpcCallback rpcCallback;

    public RpcServerAsyncConnector(String host, int port, RpcCallback rpcCallback) {
        super(host, port);
        this.rpcCallback = rpcCallback;
    }

    @Override
    public AbstractClientRemoteExecutor getClientRemoteExecutor() {
        AsyncClientRemoteExecutor remoteExecutor = new AsyncClientRemoteExecutor(connector, rpcCallback);
        return remoteExecutor;
    }
}
