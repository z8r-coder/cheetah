package rpc.demo.rpc.async;

import org.apache.log4j.Logger;
import rpc.async.RpcCallback;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc
 */
public class TestCallBack implements RpcCallback<String>{
    private Logger logger = Logger.getLogger(TestCallBack.class);
    private int i = 1;
    public void success(java.lang.String resp) {
        logger.info("this is callback " + resp);
    }

    public void fail(Throwable t) {
        logger.error(t);
    }
}
