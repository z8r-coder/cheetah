package rpc.wrapper.connector;

import rpc.client.AbstractClientRemoteExecutor;
import rpc.client.SyncClientRemoteExecutor;
import rpc.wrapper.RpcConnectorWrapper;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc
 */
public class RpcServerSyncConnector extends RpcConnectorWrapper {

    public RpcServerSyncConnector(String host, int port) {
        super(host, port);
    }

    @Override
    public AbstractClientRemoteExecutor getClientRemoteExecutor() {
        SyncClientRemoteExecutor remoteExecutor = new SyncClientRemoteExecutor(connector);
        return remoteExecutor;
    }
}
