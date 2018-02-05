package rpc.sync;


import rpc.RpcObject;

/**
 * 同步接口
 * @Author:Roy
 * @Date: Created in 18:49 2017/12/2 0002
 */
public interface RpcSync {
    /**
     * 同步等待执行结果
     */
    void waitForResult(int time, RpcCallSync sync);

    /**
     * 通知结果返回
     */
    void notifyResult(RpcCallSync sync, RpcObject rpc);
}
