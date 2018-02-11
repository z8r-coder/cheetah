package rpc.registry;

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

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc start the service to register
 */
public class ServerProxy extends RpcNioAcceptor{

    private Logger logger = Logger.getLogger(ServerProxy.class);

    private Configuration configuration;
    private IServerRegisterInfo registerInfo;
    //本地地址
    CheetahAddress cheetahAddress;

    public ServerProxy () {
        this(null, new Configuration());
    }

    public ServerProxy (AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector);
        this.configuration = configuration;
    }
    public void startService() {
        //heart beat
        heartBeatServiceRegister();
        super.startService();

        //register server ip
        cheetahAddress = new CheetahAddress(getHost(), getPort());

        String registerHost = configuration.getRegisterHost();
        int registerPort = configuration.getRegisterPort();

        logger.info("The registry's address is " + registerHost + ":" + registerPort);

        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(registerHost, registerPort, connector);
        SimpleClientRemoteExecutor remoteExecutor = new SimpleClientRemoteExecutor(connector);

        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(remoteExecutor);
        proxy.startService();

        registerInfo = proxy.registerRemote(IServerRegisterInfo.class);
        registerInfo.register(cheetahAddress);
    }

    public void stopService() {
        super.stopService();
        registerInfo.unRegister(cheetahAddress);
    }

    private void heartBeatServiceRegister() {
        SimpleServerRemoteExecutor serverRemoteExecutor = new SimpleServerRemoteExecutor();

        RpcServiceProvider provider = new RpcServiceProvider(serverRemoteExecutor);
        HeartBeatResponse response = new HeartBeatResponse(HeartBeatType.Register);
        RegisterHeartBeat registerHeartBeat = new RegisterHeartBeat(response);

        serverRemoteExecutor.registerRemote(IRegisterHeartBeat.class, registerHeartBeat);
        this.addRpcCallListener(provider);
    }
}
