package rpc;


import rpc.constants.RpcType;

/**
 * Rpc请求执行，客户端代理执行，服务端提供真正执行
 * @Author:Roy
 * @Date: Created in 17:30 2017/12/2 0002
 */
public interface RemoteExecutor extends RpcService {

    void oneWay(RemoteCall remoteCall);

    Object invoke(RemoteCall call);

    int ONEWAY = RpcType.ONEWAY.getType();

    int INVOKE = RpcType.INVOKE.getType();

}
