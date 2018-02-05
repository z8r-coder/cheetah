package rpc.net;


import rpc.RpcService;
import rpc.constants.ErrorCodeEnum;
import rpc.exception.RpcException;

/**
 * @Author:Roy
 * @Date: Created in 0:14 2017/10/15 0015
 */
public abstract class AbstractRpcAcceptor extends RpcNetBase implements RpcService {
    protected boolean stop = false;

    public void startService() {
        super.startService();
        this.setExecutorSharable(false);
    }

    public void stopService() {
        this.fireCloseNetListeners(new RpcException(ErrorCodeEnum.RPC00020));
        super.stopService();
    }
}
