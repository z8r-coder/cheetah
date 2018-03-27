package rpc.client;

import org.apache.log4j.Logger;
import rpc.RemoteCall;
import rpc.RpcObject;
import rpc.RpcSender;
import rpc.async.RpcAsyncBean;
import rpc.async.RpcCallAsync;
import rpc.async.RpcCallback;
import rpc.async.SimpleRpcCallAsync;
import rpc.constants.RpcType;
import rpc.exception.RpcException;
import rpc.net.AbstractRpcConnector;
import rpc.sync.RpcCallSync;
import rpc.sync.RpcSync;
import rpc.sync.SimpleFutureRpcSync;

import java.util.List;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc 异步RPC调用器
 */
public abstract class AsyncClientRemoteExecutor extends AbstractClientRemoteExecutor {

    private Logger logger = Logger.getLogger(SyncClientRemoteExecutor.class);

    private RpcCallAsync clientRpcAsync;
    private RpcCallback rpcCallback;
    private AbstractRpcConnector connector;
    private List<AbstractRpcConnector> connectors;

    public AsyncClientRemoteExecutor (AbstractRpcConnector connector, RpcCallback rpcCallback) {
        super();
        this.rpcCallback = rpcCallback;
        connector.addRpcCallListener(this);
        this.connector = connector;
    }

    public AsyncClientRemoteExecutor(List<AbstractRpcConnector> connectors) {
        super();
        for (AbstractRpcConnector connector : connectors) {
            connector.addRpcCallListener(this);
        }
        this.connectors = connectors;
    }

    public Object invoke(RemoteCall call) {
        AbstractRpcConnector connector = getRpcConnector();
        byte[] buffer = serializer.serialize(call);
        int length = buffer.length;
        RpcObject request = new RpcObject(INVOKE, this.getIndex(), length, buffer);
        RpcAsyncBean async = new RpcAsyncBean(request.getIndex(), request);
        rpcAsynCache.put(this.makeRpcCallCacheKey(request.getThreadId(), request.getIndex()), async);
        connector.sendRpcObject(request, timeout);

        //异步调用只管回调
        return null;
    }

    public void onRpcMessage(RpcObject rpc, RpcSender sender) {
        RpcAsyncBean rpcAsync = rpcAsynCache.get(this.makeRpcCallCacheKey(rpc.getThreadId(), rpc.getIndex()));
        clientRpcAsync = new SimpleRpcCallAsync(rpcCallback, rpc, rpcAsync);
        //回调
        clientRpcAsync.callBack();
        rpcAsynCache.remove(makeRpcCallCacheKey(rpc.getThreadId(), rpc.getIndex()));
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
