package rpc;

/**
 * @Author:Roy
 * @Date: Created in 22:51 2017/10/14 0014
 */
public interface RpcSender {

    public boolean sendRpcObject(RpcObject rpcObject, int timeOut);
}
