package rpc.client;

import rpc.RemoteCall;
import rpc.RemoteExecutor;
import rpc.Service;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcCallListener;

import java.util.List;

/**
 * @Author:Roy
 * @Date: Created in 14:51 2017/12/3 0003
 */
public class SimpleClientRemoteExecutor extends AbstractClientRemoteExecutor implements RemoteExecutor, RpcCallListener, Service {

    private AbstractRpcConnector connector;

    private List<AbstractRpcConnector> connectors;

    public SimpleClientRemoteExecutor(AbstractRpcConnector connector) {
        super();
        connector.addRpcCallListener(this);
        this.connector = connector;
    }

    public SimpleClientRemoteExecutor(List<AbstractRpcConnector> connectors) {
        super();
        for (AbstractRpcConnector connector : connectors) {
            connector.addRpcCallListener(this);
        }
        this.connectors = connectors;
    }


    public AbstractRpcConnector getConnector() {
        return connector;
    }

    public AbstractRpcConnector getRpcConnector() {
        return connector;
    }

    public List<AbstractRpcConnector> getRpcConnectors () {
        return connectors;
    }

    public void startService() {
        if (connector != null) {
            connector.startService();
        } else {
            for (AbstractRpcConnector connector : connectors) {
                connector.startService();
            }
        }
    }

    public void stopService() {
        if (connector != null) {
            connector.stopService();
        } else {
            for (AbstractRpcConnector connector : connectors) {
                connector.stopService();
            }
        }
    }
}
