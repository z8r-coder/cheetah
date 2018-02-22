package rpc.registry;

import constants.Globle;
import constants.HeartBeatType;
import models.CheetahAddress;
import models.HeartBeatResponse;
import org.apache.log4j.Logger;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;
import rpc.nio.RpcNioConnector;
import rpc.server.RpcServiceProvider;
import rpc.server.SimpleServerRemoteExecutor;
import rpc.utils.RpcUtils;
import utils.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc start the service to register
 */
public class ServerProxy extends RpcNioAcceptor{

    private Logger logger = Logger.getLogger(ServerProxy.class);

    //heart beat
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Configuration configuration;
    private IServerRegisterInfo registerInfo;

    private String address;
    public ServerProxy () {
        this(null, new Configuration());
    }

    public ServerProxy (AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector);
        this.configuration = configuration;
    }
    public void startService() {
        address = getHost() + ":" + getPort();

        super.startService();

        //register server ip
        String registerHost = configuration.getRegisterHost();
        int registerPort = configuration.getRegisterPort();

        logger.info("The registry's address is " + registerHost + ":" + registerPort);

        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(registerHost, registerPort, connector);
        SimpleClientRemoteExecutor remoteExecutor = new SimpleClientRemoteExecutor(connector);

        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(remoteExecutor);
        proxy.startService();

        //register server ip
        registerInfo = proxy.registerRemote(IServerRegisterInfo.class);
        registerInfo.register(address);

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                registerInfo.heartBeat(address);
            }
        }, Globle.REG_HEART_BEAT_INIT_TEST, Globle.REG_HEART_BEAT_INTERVAL_TEST, TimeUnit.SECONDS);
    }

    public void stopService() {
        super.stopService();
        registerInfo.unRegister(address);
    }
}
