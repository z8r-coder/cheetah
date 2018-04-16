package rpc.wrapper;

import rpc.net.AbstractRpcAcceptor;
import rpc.net.AbstractRpcNetworkBase;
import rpc.nio.RpcNioAcceptor;
import rpc.server.RpcServiceProvider;
import rpc.server.SimpleServerRemoteExecutor;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc
 */
public abstract class RpcAcceptorWrapper extends AbstractRpcNetworkBase {
    protected AbstractRpcAcceptor acceptor;
    protected SimpleServerRemoteExecutor remoteExecutor;

    public RpcAcceptorWrapper(String host, int port) {
        this.setHost(host);
        this.setPort(port);
    }

    @Override
    public void startService() {
        acceptor = new RpcNioAcceptor();
        RpcUtils.setAddress(getHost(), getPort(), acceptor);
        remoteExecutor = new SimpleServerRemoteExecutor();
        RpcServiceProvider provider = new RpcServiceProvider(remoteExecutor);
        //register which def by coder
        register();
        acceptor.addRpcCallListener(provider);
        acceptor.startService();
    }

    @Override
    public void stopService() {
        acceptor.stopService();
    }

    public abstract void register();
}
