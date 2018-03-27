package rpc.async;

import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.exception.RpcException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc rpc异步调用实现
 */
public class SimpleRpcCallAsync implements RpcCallAsync{

    private Logger logger = Logger.getLogger(SimpleRpcCallAsync.class);

    private RpcCallback rpcCallback;
    private RpcObject resp;
    private RpcAsyncBean rpcAsync;
    private ExecutorService executorService = Executors.newScheduledThreadPool(3);

    public SimpleRpcCallAsync(RpcCallback rpcCallback, RpcObject resp, RpcAsyncBean rpcAsync) {
        this.rpcCallback = rpcCallback;
        this.resp = resp;
        this.rpcAsync = rpcAsync;
    }

    public void callBack() {
        executorService.execute(new Runnable() {
            public void run() {
                if (resp != null && rpcAsync.getRequest().getThreadId() == resp.getThreadId()) {
                    logger.error("callBack success!");
                    rpcCallback.success(resp);
                } else {
                    logger.error("callBack fail!");
                    rpcCallback.fail(new RpcException("resp=" + resp + " request threadId=" +
                    rpcAsync.getRequest().getThreadId() + " resp threadId=" +
                    resp.getThreadId()));
                }
            }
        });
    }
}
