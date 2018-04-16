package rpc.wrapper;

import rpc.client.AbstractClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.net.AbstractRpcNetworkBase;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc
 */
public abstract class RpcConnectorWrapper extends AbstractRpcNetworkBase {

    protected AbstractRpcConnector connector;
    private AbstractClientRemoteExecutor clientRemoteExecutor;
    private SimpleClientRemoteProxy proxy;

    public RpcConnectorWrapper (String host, int port) {
        this.setHost(host);
        this.setPort(port);
    }

    @Override
    public void startService() {
        connector = new RpcNioConnector(null);
        RpcUtils.setAddress(getHost(), getPort(), connector);
        clientRemoteExecutor = getClientRemoteExecutor();
        proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
        proxy.startService();
    }

    @Override
    public void stopService() {
        proxy.stopService();
    }

    public SimpleClientRemoteProxy getProxy() {
        return proxy;
    }

    public abstract AbstractClientRemoteExecutor getClientRemoteExecutor ();
}
