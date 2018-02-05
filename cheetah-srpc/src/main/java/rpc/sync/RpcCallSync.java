package rpc.sync;

import rpc.RpcObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author:Roy
 * @Date: Created in 18:49 2017/12/2 0002
 */
public class RpcCallSync implements Future {

    private int index;

    /**
     * 请求发送包
     */
    private RpcObject request;
    /**
     * 请求返回数据包
     */
    private RpcObject response;

    public RpcCallSync(int index, RpcObject request) {
        this.index = index;
        this.request = request;
    }


    public int getIndex() {
        return index;
    }

    public RpcObject getRequest() {
        return request;
    }

    public RpcObject getResponse() {
        return response;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRequest(RpcObject request) {
        this.request = request;
    }

    public void setResponse(RpcObject response) {
        this.response = response;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return response != null;
    }

    public Object get() throws InterruptedException, ExecutionException {
        return response;
    }

    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
