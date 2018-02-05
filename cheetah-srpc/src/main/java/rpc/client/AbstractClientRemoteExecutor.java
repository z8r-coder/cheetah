package rpc.client;

import org.apache.log4j.Logger;
import rpc.*;
import rpc.exception.RpcException;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcCallListener;
import rpc.serializer.JdkSerializer;
import rpc.serializer.RpcSerializer;
import rpc.sync.RpcCallSync;
import rpc.sync.RpcSync;
import rpc.sync.SimpleFutureRpcSync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:Roy
 * @Date: Created in 18:47 2017/12/2 0002
 */
public abstract class AbstractClientRemoteExecutor implements RemoteExecutor, RpcService, RpcCallListener {
    protected int timeout = 10000;
    private AtomicInteger index = new AtomicInteger(10000);
    private RpcSync clientRpcSync;
    private RpcSerializer serializer;

    private Logger logger = Logger.getLogger(AbstractClientRemoteExecutor.class);

    private Map<String, RpcCallSync> rpcCache = new ConcurrentHashMap<String, RpcCallSync>();

    public AbstractClientRemoteExecutor() {
        clientRpcSync = new SimpleFutureRpcSync();
        serializer = new JdkSerializer();
    }
    public void oneWay(RemoteCall remoteCall) {
        AbstractRpcConnector connector = getRpcConnector(remoteCall);
        byte[] buffer = serializer.serialize(remoteCall);
        int length = buffer.length;
        RpcObject rpc = new RpcObject(ONEWAY, this.getIndex(), length, buffer);
        connector.sendRpcObject(rpc, timeout);
    }

    public Object invoke(RemoteCall call) {
        AbstractRpcConnector connector = getRpcConnector(call);
        byte[] buffer = serializer.serialize(call);
        int length = buffer.length;
        RpcObject request = new RpcObject(INVOKE, this.getIndex(), length, buffer);
        RpcCallSync sync = new RpcCallSync(request.getIndex(), request);
        rpcCache.put(this.makeRpcCallCacheKey(request.getThreadId(), request.getIndex()), sync);
        connector.sendRpcObject(request, timeout);
        clientRpcSync.waitForResult(timeout, sync);
        rpcCache.remove(sync.getIndex());
        RpcObject response = sync.getResponse();
        if (response == null) {
            throw new RpcException("rpc response == null");
        }

        if (response.getLength() > 0) {
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

    private String makeRpcCallCacheKey(long threadId, int index) {
        return "rpc_" + threadId + "_" + index;
    }

    public int getIndex() {
        return index.getAndIncrement();
    }

    public abstract AbstractRpcConnector getRpcConnector(RemoteCall call);

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }
}
