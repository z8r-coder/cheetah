package rpc.async;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc
 */
public interface RPCCallback<V> {
    /**
     * 异步成功
     * @param resp
     */
    void success(V resp);

    /**
     * 异步失败
     * @param t
     */
    void fail(Throwable t);
}
