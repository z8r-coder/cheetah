package rpc.nio;

import rpc.Service;
import rpc.exception.RpcNetExceptionHandler;
import rpc.net.RpcNetBase;
import rpc.net.RpcNetListener;
import rpc.net.RpcOutputNofity;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author:Roy
 * @Date: Created in 0:12 2017/10/15 0015
 */
public abstract class AbstractRpcNioSelector implements Service, RpcOutputNofity, RpcNetExceptionHandler {

    protected List<RpcNetListener> netListeners;

    public abstract void register(RpcNioAcceptor acceptor);

    public abstract void unRegister(RpcNioAcceptor acceptor);

    public abstract void register(RpcNioConnector connector);

    public abstract void unRegister(RpcNioConnector connector);

    public AbstractRpcNioSelector() {
        netListeners = new LinkedList<RpcNetListener>();
    }

    public void addRpcNetListener(RpcNetListener listener) {
        netListeners.add(listener);
    }

    public void fireNetListeners(RpcNetBase netWork, Exception e) {
        for (RpcNetListener listener : netListeners) {
            listener.onClose(netWork, e);
        }
    }
}
