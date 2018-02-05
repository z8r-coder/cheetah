package rpc.exception;

import org.apache.log4j.Logger;
import rpc.RemoteCall;
import rpc.RpcObject;

/**
 * 异常处理器
 * @Author:Roy
 * @Date: Created in 17:25 2017/12/3 0003
 */
public class SimpleRpcExceptionHandler implements RpcExceptionHandler {

    private Logger logger = Logger.getLogger(SimpleRpcExceptionHandler.class);

    public void handleException(RpcObject rpc, RemoteCall call, Throwable e) {
        if (e instanceof RpcException) {
            logger.info("rpcException" + e.getMessage());
        } else {
            e.printStackTrace();
        }
    }
}
