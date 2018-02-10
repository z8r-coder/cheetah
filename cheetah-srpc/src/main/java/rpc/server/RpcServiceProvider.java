package rpc.server;


import org.apache.log4j.Logger;
import rpc.*;
import rpc.constants.RpcType;
import rpc.exception.RpcExceptionHandler;
import rpc.exception.SimpleRpcExceptionHandler;
import rpc.filter.RpcFilter;
import rpc.filter.RpcFilterChain;
import rpc.filter.SimpleRpcFilterChain;
import rpc.net.RpcCallListener;
import rpc.serializer.JdkSerializer;
import rpc.serializer.RpcSerializer;
import rpc.utils.RpcUtils;

/**
 * 接受rpcObject
 * @Author:Roy
 * @Date: Created in 17:18 2017/12/3 0003
 */
public class RpcServiceProvider implements RpcCallListener, RpcFilter, RpcService {

    private Logger logger = Logger.getLogger(RpcServiceProvider.class);

    /**
     * 提交给上层的执行器
     */
    private RemoteExecutor executor;

    private RpcSerializer serializer;

    /**
     * 超时时间
     */
    private int timeout = 200;

    private RpcExceptionHandler exceptionHandler;

    private RpcFilterChain filterChain;

    public RpcServiceProvider(RemoteExecutor executor) {
        serializer = new JdkSerializer();
        exceptionHandler = new SimpleRpcExceptionHandler();
        filterChain = new SimpleRpcFilterChain();
        this.executor = executor;
    }
    public void startService() {
        filterChain.addRpcFilter(this);
    }

    public void stopService() {

    }

    public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain) {
        int index = rpc.getIndex();
        if (rpc.getType() == RpcType.ONEWAY) {
            executor.oneWay(call);
        } else if (rpc.getType() == RpcType.INVOKE) {
            this.execute(call, rpc.getThreadId(), index, sender);
        }
    }

    /**
     * 同步调用执行
     */
    private void execute(RemoteCall call, long threadId, int index, RpcSender sender) {
        RpcObject rpc = this.createRpcObject(index);
        rpc.setThreadId(threadId);
        //执行提交给上层应用
        Object result = executor.invoke(call);
        rpc.setType(RpcType.SUC);
        if (result != null) {
            byte[] data = serializer.serialize(result);
            rpc.setLength(data.length);
            rpc.setData(data);
        } else {
            rpc.setLength(0);
            rpc.setData(new byte[0]);
        }
        //结果返回
        sender.sendRpcObject(rpc, timeout);
    }

    /**
     * tcp接收到rpcObject后调用该方法
     */
    public void onRpcMessage(RpcObject rpc, RpcSender sender) {
        RemoteCall call = this.deserializeCall(rpc, sender);
        //服务提供方得到上下文
        RpcContext.getContext().putAll(call.getAttachment());
        try {
            if (call != null) {
                filterChain.startFilter(rpc, call, sender);
            }
        } catch (Exception e) {
            //出现异常直接返回异常
            this.handleException(rpc, call, sender, e);
        }
    }

    /**
     * 反序列化
     */
    private RemoteCall deserializeCall(RpcObject rpc, RpcSender sender) {
        try {
            return (RemoteCall) serializer.deserialize(rpc.getData());
        } catch (Exception e) {
            this.handleException(rpc, null, sender, e);
            return null;
        }

    }

    private RpcObject createRpcObject(int index) {
        return new RpcObject(0, index, 0, null);
    }

    private void handleException(RpcObject rpc, RemoteCall call, RpcSender sender, Exception e) {
        RpcUtils.handleException(exceptionHandler,rpc,call,e);
        if(rpc.getType()== RpcType.INVOKE){
            //生成异常数据
            RpcObject respRpc = this.createRpcObject(rpc.getIndex());
            respRpc.setThreadId(rpc.getThreadId());
            respRpc.setType(RpcType.FAIL);
            String message = e.getMessage();
            if(message!=null){
                byte[] data = message.getBytes();
                respRpc.setLength(data.length);
                if(data.length > 0){
                    respRpc.setData(data);
                }
            }
            //调用失败异常返回
            sender.sendRpcObject(respRpc, timeout);
        }
    }

    public RemoteExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(RemoteExecutor executor) {
        this.executor = executor;
    }

    public RpcExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(RpcExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public void addRpcFilter(RpcFilter filter){
        filterChain.addRpcFilter(filter);
    }

    public void setFilterChain(RpcFilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
