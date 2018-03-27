package rpc.async;

import rpc.RpcObject;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc
 */
public class RpcAsyncBean {
    /**
     * thread seq, protocol field
     */
    private int index;

    /**
     * request
     */
    private RpcObject request;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public RpcObject getRequest() {
        return request;
    }

    public void setRequest(RpcObject request) {
        this.request = request;
    }
}
