package rpc.registry;

import models.CheetahAddress;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;
import utils.ParseUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class SimpleRegisterServer extends RpcNioAcceptor {

    private IServerRegisterInfo registerInfo;
    private ScheduledExecutorService heartBeatExecutor = Executors.newScheduledThreadPool(1);

    public SimpleRegisterServer (IServerRegisterInfo registerInfo) {
        this(null, registerInfo);
    }
    public SimpleRegisterServer (AbstractRpcNioSelector selector, IServerRegisterInfo registerInfo) {
        super(selector);
        this.registerInfo = registerInfo;
    }

    public void startService() {
        super.startService();
        heartBeatExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Set<String> serverList = registerInfo.getServerList();
                Set<CheetahAddress> remoteAddress = new HashSet<CheetahAddress>();
                for (String server : serverList) {
                    CheetahAddress cheetahAddress = ParseUtils.parseAddress(server);
                    remoteAddress.add(cheetahAddress);
                }

            }
        }, 0, 50, TimeUnit.SECONDS);
    }

    public void stopService() {
        super.stopService();
        heartBeatExecutor.shutdown();
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println(111);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
