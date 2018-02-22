package rpc.registry;

import constants.Globle;
import constants.HeartBeatType;
import models.CheetahAddress;
import models.HeartBeatRequest;
import models.HeartBeatResponse;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import sun.net.www.ParseUtil;
import utils.ParseUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
                registerInfo.updateList();

                // TODO: 2018/2/22 test
                Set<String> serverList = registerInfo.getServerList();
                for (String address : serverList) {
                    System.out.println(address);
                }
            }
        }, 0, Globle.REG_UPDATE_SERVER_LIST_TEST, TimeUnit.SECONDS);
    }

    public void stopService() {
        super.stopService();
        heartBeatExecutor.shutdown();
    }

    public List<CheetahAddress> getServerList() {
        if (registerInfo != null) {
            Set<String> serverList = registerInfo.getServerList();
            List<CheetahAddress> cheetahAddressList = ParseUtils.parseListAddress(serverList);
            return cheetahAddressList;
        }
        return new ArrayList<CheetahAddress>();
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
