package rpc.net;

import rpc.RpcObject;
import rpc.RpcSender;
import rpc.RpcService;
import rpc.exception.RpcNetExceptionHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author:Roy
 * @Date: Created in 0:18 2017/10/15 0015
 */
public abstract class RpcNetBase extends AbstractRpcNetworkBase implements RpcNetExceptionHandler {
    private ExecutorService executorService;
    private boolean executorSharable;

    //receive tcp data to handle
    private List<RpcCallListener> callListeners;
    private List<RpcNetListener> netListeners;

    private final static int DEFAULT_EXECUTOR_THREAD_COUNT = 3;
    //执行器数量
    private int executorThreadCount = DEFAULT_EXECUTOR_THREAD_COUNT;

    public RpcNetBase() {
        callListeners = new LinkedList<RpcCallListener>();
        netListeners = new LinkedList<RpcNetListener>();
    }

    public void addRpcCallListener(RpcCallListener listener) {
        callListeners.add(listener);
    }

    //receive tcp data
    public void fireCallListeners(RpcObject rpc, RpcSender sender) {
        for (RpcCallListener listener : callListeners) {
            listener.onRpcMessage(rpc, sender);
        }
    }

    public void startListeners() {
        for (RpcCallListener listener : callListeners) {
            if (listener instanceof RpcService) {
                RpcService rpcService = (RpcService) listener;
                rpcService.startService();
            }
        }
    }

    public void stopListeners() {
        for (RpcCallListener listener : callListeners) {
            if (listener instanceof RpcService) {
                RpcService rpcService = (RpcService) listener;
                rpcService.stopService();
            }
        }
    }

    public void addConnectorListener(AbstractRpcConnector connector) {
        for (RpcCallListener listener : callListeners) {
            connector.addRpcCallListener(listener);
        }
    }

    public void addRpcNetListener(RpcNetListener listener) {
        netListeners.add(listener);
    }

    public void fireCloseNetListeners(Exception e) {
        for (RpcNetListener listener : netListeners) {
            listener.onClose(this, e);
        }
    }

    public void fireStartNetListeners() {
        for (RpcNetListener listener : netListeners) {
            listener.onStart(this);
        }
    }

    public void startService() {
        if (this.executorService == null) {
            if (this.executorThreadCount < 1) {
                this.executorThreadCount = DEFAULT_EXECUTOR_THREAD_COUNT;
            }
            executorService = Executors.newFixedThreadPool(executorThreadCount);
        }
    }

    public void stopService() {
        if (!this.isExecutorSharable() && executorService != null) {
            executorService.shutdown();
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public boolean isExecutorSharable() {
        return executorSharable;
    }

    public void setExecutorSharable(boolean executorSharable) {
        this.executorSharable = executorSharable;
    }

    public List<RpcCallListener> getCallListeners() {
        return callListeners;
    }

    public void setCallListeners(List<RpcCallListener> callListeners) {
        this.callListeners = callListeners;
    }

    public List<RpcNetListener> getNetListeners() {
        return netListeners;
    }

    public void setNetListeners(List<RpcNetListener> netListeners) {
        this.netListeners = netListeners;
    }

    public static int getDefaultExecutorThreadCount() {
        return DEFAULT_EXECUTOR_THREAD_COUNT;
    }

    public int getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }
}
