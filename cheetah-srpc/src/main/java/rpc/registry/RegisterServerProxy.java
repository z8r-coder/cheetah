package rpc.registry;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioAcceptor;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc start the service to register
 */
public class RegisterServerProxy extends RpcNioAcceptor{

    private Logger logger = Logger.getLogger(RegisterServerProxy.class);

    private Configuration configuration;
    private IServerRegisterInfo registerInfo;
    //本地地址
    CheetahAddress cheetahAddress;

    public RegisterServerProxy () {
        this(null, new Configuration());
    }

    public RegisterServerProxy (AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector);
        this.configuration = configuration;
    }
    public void startService() {
        super.startService();

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
}
