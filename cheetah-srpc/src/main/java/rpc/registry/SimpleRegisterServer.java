package rpc.registry;

import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class SimpleRegisterServer extends RpcNioAcceptor {

    private List<String> serverList = new CopyOnWriteArrayList<String>();
    private ScheduledExecutorService heartBeatExecutor = Executors.newScheduledThreadPool(1);

    public SimpleRegisterServer () {
        this(null);
    }
    public SimpleRegisterServer (AbstractRpcNioSelector selector) {
        super(selector);
    }

    public void startService() {
        super.startService();
        heartBeatExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopService() {
        super.stopService();
        serverList.clear();
        heartBeatExecutor.shutdown();
    }
}
