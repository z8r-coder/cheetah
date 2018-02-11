package rpc.registry;

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
    private SimpleClientRemoteExecutor remoteExecutor;
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
                List<CheetahAddress> serverList = registerInfo.getServerList();
                List<AbstractRpcConnector> connectors = new ArrayList<AbstractRpcConnector>();

                for (CheetahAddress cheetahAddress : serverList) {
//                    System.out.println(cheetahAddress.getHost() + ":" + cheetahAddress.getPort());
                    AbstractRpcConnector connector = new RpcNioConnector(null);
                    RpcUtils.setAddress(cheetahAddress.getHost(), cheetahAddress.getPort(), connector);
                    connectors.add(connector);
                }
                if (remoteExecutor == null) {
                    remoteExecutor = new SimpleClientRemoteExecutor(connectors);
                } else {
                    //stop last time connectors service
                    remoteExecutor.stopService();
                    remoteExecutor = new SimpleClientRemoteExecutor(connectors);
                }

                SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(remoteExecutor);
                proxy.startService();

                IRegisterHeartBeat registerHeartBeat = proxy.registerRemote(IRegisterHeartBeat.class);
                HeartBeatRequest request = new HeartBeatRequest(HeartBeatType.Register);

                HeartBeatResponse response = registerHeartBeat.registerHeartBeat(request);
                if (response != null &&
                        response.getHeartBeatType() == HeartBeatType.Register) {

                }
                // TODO: 2018/2/10 heatBeat
            }
        }, 0, 3, TimeUnit.SECONDS);
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
