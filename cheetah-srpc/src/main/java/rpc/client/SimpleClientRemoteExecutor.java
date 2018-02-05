package rpc.client;

import rpc.RemoteCall;
import rpc.RemoteExecutor;
import rpc.RpcService;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcCallListener;

/**
 * @Author:Roy
 * @Date: Created in 14:51 2017/12/3 0003
 */
public class SimpleClientRemoteExecutor extends AbstractClientRemoteExecutor implements RemoteExecutor, RpcCallListener, RpcService {

    private AbstractRpcConnector connector;

    public SimpleClientRemoteExecutor(AbstractRpcConnector connector) {
        super();
        connector.addRpcCallListener(this);
        this.connector = connector;
    }

    public AbstractRpcConnector getConnector() {
        return connector;
    }

    public AbstractRpcConnector getRpcConnector(RemoteCall call) {
        return connector;
    }

    public void startService() {
        connector.startService();
    }

    public void stopService() {
        connector.stopService();
    }
}
