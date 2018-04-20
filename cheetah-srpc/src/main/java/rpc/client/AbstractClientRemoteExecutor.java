package rpc.client;

import org.apache.log4j.Logger;
import rpc.RemoteCall;
import rpc.RemoteExecutor;
import rpc.RpcObject;
import rpc.Service;
import rpc.async.RpcAsyncBean;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcCallListener;
import rpc.serializer.JdkSerializer;
import rpc.serializer.RpcSerializer;
import rpc.sync.RpcCallSync;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:Roy
 * @Date: Created in 18:47 2017/12/2
 */
public abstract class AbstractClientRemoteExecutor implements RemoteExecutor, Service, RpcCallListener {
    protected int timeout = 10000;
    private AtomicInteger index = new AtomicInteger(10000);
    protected RpcSerializer serializer;

    private Logger logger = Logger.getLogger(AbstractClientRemoteExecutor.class);

    //同步作用集cache
    protected Map<String, RpcCallSync> rpcCache = new ConcurrentHashMap<String, RpcCallSync>();
    //异步作用集cache
    protected Map<String, RpcAsyncBean> rpcAsynCache = new ConcurrentHashMap<String, RpcAsyncBean>();

    public AbstractClientRemoteExecutor() {
        serializer = new JdkSerializer();
    }
    public void oneWay(RemoteCall remoteCall) {
        AbstractRpcConnector connector = getRpcConnector();
        byte[] buffer = serializer.serialize(remoteCall);
        int length = buffer.length;
        RpcObject rpc = new RpcObject(ONEWAY, this.getIndex(), length, buffer);
        connector.sendRpcObject(rpc, timeout);
    }

    public void oneWayBroadcast(RemoteCall remoteCall) {
        List<AbstractRpcConnector> connectors = getRpcConnectors();
        byte[] buffer = serializer.serialize(remoteCall);
        int length = buffer.length;
        RpcObject rpc = new RpcObject(ONEWAY, this.getIndex(), length, buffer);
        for (AbstractRpcConnector connector : connectors) {
            connector.sendRpcObject(rpc, timeout);
        }
    }

    protected String makeRpcCallCacheKey(long threadId, int index) {
        return "rpc_" + threadId + "_" + index;
    }

    public int getIndex() {
        return index.getAndIncrement();
    }

    public abstract AbstractRpcConnector getRpcConnector();

    public abstract List<AbstractRpcConnector> getRpcConnectors();

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }
}
