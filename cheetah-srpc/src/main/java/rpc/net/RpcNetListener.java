package rpc.net;

/**
 * 网络异常监听器
 * @Author:Roy
 * @Date: Created in 0:29 2017/10/15 0015
 */
public interface RpcNetListener {

    public void onClose(RpcNetBase netWork, Exception e);

    public void onStart(RpcNetBase netWork);
}
