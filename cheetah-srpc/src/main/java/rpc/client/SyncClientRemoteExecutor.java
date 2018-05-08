package rpc.client;

import constants.ErrorCodeEnum;
import org.apache.log4j.Logger;
import rpc.RemoteCall;
import rpc.RpcObject;
import rpc.RpcSender;
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
 * @desc 同步RPC调用器
 */
public class SyncClientRemoteExecutor extends AbstractClientRemoteExecutor {

    private Logger logger = Logger.getLogger(SyncClientRemoteExecutor.class);

    private RpcSync clientRpcSync;
    private AbstractRpcConnector connector;
    private List<AbstractRpcConnector> connectors;

    public SyncClientRemoteExecutor(AbstractRpcConnector connector) {
        super();
        clientRpcSync = new SimpleFutureRpcSync();
        connector.addRpcCallListener(this);
        this.connector = connector;
    }

    public SyncClientRemoteExecutor(List<AbstractRpcConnector> connectors) {
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
        RpcCallSync sync = new RpcCallSync(request.getIndex(), request);
        rpcCache.put(makeRpcCallCacheKey(request.getThreadId(), request.getIndex()), sync);
        connector.sendRpcObject(request, timeout);
        clientRpcSync.waitForResult(timeout, sync);
        rpcCache.remove(makeRpcCallCacheKey(request.getThreadId(), request.getIndex()));
        RpcObject response = sync.getResponse();
        if (response == null) {
            throw new RpcException(ErrorCodeEnum.RPC00008,
                    ErrorCodeEnum.RPC00008.getErrorDesc());
        }

        if (response.getType() != RpcType.FAIL && response.getLength() > 0) {
            return serializer.deserialize(sync.getResponse().getData());
        }
        return null;
    }

    public void onRpcMessage(RpcObject rpc, RpcSender sender) {
        RpcCallSync sync = rpcCache.get(this.makeRpcCallCacheKey(rpc.getThreadId(), rpc.getIndex()));
        if (sync != null && sync.getRequest().getThreadId() == rpc.getThreadId()) {
            clientRpcSync.notifyResult(sync, rpc);
        }
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
