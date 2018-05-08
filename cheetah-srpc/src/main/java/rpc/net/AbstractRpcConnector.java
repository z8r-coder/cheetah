package rpc.net;


import constants.ErrorCodeEnum;
import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.RpcSender;
import rpc.Service;
import rpc.exception.RpcException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @Author:Roy
 * @Date: Created in 22:55 2017/10/14 0014
 */
public abstract class AbstractRpcConnector extends RpcNetBase implements Service, RpcSender {

    protected boolean stop = false;
    private Logger logger = Logger.getLogger(AbstractRpcConnector.class);
    protected String remoteHost;
    protected int remotePort;
    protected ConcurrentHashMap<String, Object> rpcContext;

    private RpcOutputNofity outputNofity;

    private AbstractRpcWriter writer;

    public AbstractRpcConnector(AbstractRpcWriter rpcWriter) {
        super();
        this.writer = rpcWriter;
        rpcContext = new ConcurrentHashMap<>();
    }
    protected ConcurrentLinkedQueue<RpcObject> sendQueueCache = new ConcurrentLinkedQueue<RpcObject>();

    public boolean sendRpcObject(RpcObject rpcObject, int timeOut) {
        int cost = 0;
        while (!sendQueueCache.offer(rpcObject)) {
            cost += 3;
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
            if (timeOut > 0 && cost > timeOut) {
                throw new RpcException(ErrorCodeEnum.RPC00010,
                        ErrorCodeEnum.RPC00010.getErrorDesc());
            }
        }
        this.notifySend();
        return true;
    }

    public boolean isNeedToSend() {
        RpcObject peek = sendQueueCache.peek();
        return peek != null;
    }

    public RpcObject getToSend() {
        return sendQueueCache.poll();
    }

    public void notifySend() {
        if (writer != null) {
            writer.notifySend(this);
        }
    }

    public void fireCall(final RpcObject rpc) {
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    fireCallListeners(rpc, AbstractRpcConnector.this);
                } catch (Exception e) {
                    logger.error("fire call err:" + e.getMessage(), e);
                }

            }
        });
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public ConcurrentHashMap<String, Object> getRpcContext() {
        return rpcContext;
    }

    public void setRpcContext(ConcurrentHashMap<String, Object> rpcContext) {
        this.rpcContext = rpcContext;
    }

    public RpcOutputNofity getOutputNofity() {
        return outputNofity;
    }

    public void setOutputNofity(RpcOutputNofity outputNofity) {
        this.outputNofity = outputNofity;
    }

    public AbstractRpcWriter getWriter() {
        return writer;
    }

    public void setWriter(AbstractRpcWriter writer) {
        this.writer = writer;
    }

    public ConcurrentLinkedQueue<RpcObject> getSendQueueCache() {
        return sendQueueCache;
    }

    public void setSendQueueCache(ConcurrentLinkedQueue<RpcObject> sendQueueCache) {
        this.sendQueueCache = sendQueueCache;
    }
}
