package rpc.registry;

import models.CheetahAddress;
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

    private Configuration configuration;

    public RegisterServerProxy () {
        this(null, new Configuration());
    }

    public RegisterServerProxy (AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector);
        this.configuration = configuration;
    }
    public void startService() {
        super.startService();
        //注册中心注册
        CheetahAddress cheetahAddress = new CheetahAddress(getHost(), getPort());

        String registerHost = configuration.getRegisterHost();
        int registerPort = configuration.getRegisterPort();
        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(registerHost, registerPort, connector);
        SimpleClientRemoteExecutor remoteExecutor = new SimpleClientRemoteExecutor(connector);

        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(remoteExecutor);
        
    }

    public void stopService() {

    }
}
