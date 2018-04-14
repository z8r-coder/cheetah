package rpc.registry;

import constants.Globle;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import rpc.client.SimpleClientRemoteProxy;
import rpc.client.SyncClientRemoteExecutor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import utils.Configuration;
import utils.ParseUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc start the service to register
 */
public abstract class AbstractServerProxy extends RpcNioAcceptor{

    private Logger logger = Logger.getLogger(AbstractServerProxy.class);

    //heart beat
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Configuration configuration;
    private IServerRegisterInfo registerInfo;

    //every node needs to cache serverList
    protected Map<Integer, String> cacheServerList = new ConcurrentHashMap<Integer, String>();

    protected SimpleClientRemoteProxy proxy;

    private String address;
    public AbstractServerProxy () {
        this(null, new Configuration());
    }

    public AbstractServerProxy (AbstractRpcNioSelector selector, Configuration configuration) {
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
        SyncClientRemoteExecutor remoteExecutor = new SyncClientRemoteExecutor(connector);

        proxy = new SimpleClientRemoteProxy(remoteExecutor);
        proxy.startService();

        //register server ip
        registerInfo = proxy.registerRemote(IServerRegisterInfo.class);
        cacheServerList = registerInfo.register(address);

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                cacheServerList = registerInfo.heartBeat(address);
            }
        }, Globle.REG_HEART_BEAT_INIT_TEST, Globle.REG_HEART_BEAT_INTERVAL_TEST, TimeUnit.SECONDS);

        // business
        register();
    }

    protected abstract void register();

    public void stopService() {
        super.stopService();
        registerInfo.unRegister(address);
    }

    public Map<Integer, String> getCacheServerList() {
        return cacheServerList;
    }

    public void setCacheServerList (List<CheetahAddress> addresses) {
        this.cacheServerList = new ConcurrentHashMap<>();
        for (CheetahAddress cheetahAddress : addresses) {
            Integer serverId = ParseUtils.generateServerId(cheetahAddress.getHost(), cheetahAddress.getPort());
            cacheServerList.put(serverId, cheetahAddress.toString());
        }
    }
}
