package rpc.filter;

import rpc.RemoteCall;
import rpc.RpcObject;
import rpc.RpcSender;

/**
 * @Author:Roy
 * @Date: Created in 17:19 2017/12/3 0003
 */
public interface RpcFilter {

    /**
     * 过滤
     */
    public void doFilter(RpcObject rpc, RemoteCall call, RpcSender sender, RpcFilterChain chain);
}
