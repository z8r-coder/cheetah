package rpc.exception;


import rpc.RemoteCall;
import rpc.RpcObject;

/**
 * @Author:Roy
 * @Date: Created in 14:59 2017/10/15 0015
 */
public interface RpcExceptionHandler {

    public void handleException(RpcObject rpc, RemoteCall call, Throwable e);
}
