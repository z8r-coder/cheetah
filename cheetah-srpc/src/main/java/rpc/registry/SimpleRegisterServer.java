package rpc.registry;

import constants.Globle;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
                registerInfo.updateList();

                // TODO: 2018/2/22 test
//                Map<Integer,String> serverList = registerInfo.getServerListCache();
//                for (Integer serverId : serverList.keySet()) {
//                    System.out.println("serverId:" + serverId + " ----- "
//                            + "server address:" + serverList.get(serverId));
//                }
            }
        }, 0, Globle.REG_UPDATE_SERVER_LIST_TEST, TimeUnit.SECONDS);
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
