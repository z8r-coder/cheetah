package rpc.registry;

import org.apache.log4j.Logger;
import rpc.RpcService;
import rpc.server.RpcServiceProvider;
import rpc.server.SimpleServerRemoteExecutor;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc register server
 */
public class SimpleRegisterProxy implements RpcService {

    private final static Logger logger = Logger.getLogger(SimpleRegisterProxy.class);

    private SimpleRegisterServer registerServer;
    private Configuration configuration;

    public SimpleRegisterProxy () {
        this.registerServer = new SimpleRegisterServer();
        this.configuration = new Configuration();
    }
    public SimpleRegisterProxy (SimpleRegisterServer registerServer,
                                Configuration configuration) {
        this.registerServer = registerServer;
        this.configuration = configuration;
    }

    public void startService() {
        configuration.loadPropertiesFromSrc();
        String host = configuration.getRegisterHost();
        int port = configuration.getRegisterPort();
        registerServer.setHost(host);
        registerServer.setPort(port);

        RpcServiceProvider provider = new RpcServiceProvider();
        SimpleServerRemoteExecutor remoteExecutor = new SimpleServerRemoteExecutor();
        ServerRegisterInfo registerInfo = new ServerRegisterInfo();
        remoteExecutor.registerRemote(IServerRegisterInfo.class, registerInfo);

        provider.setExecutor(remoteExecutor);
        registerServer.addRpcCallListener(provider);

        registerServer.startService();

        logger.info("Register Center has registered!");
    }

    public void stopService() {
        registerServer.stopService();
    }
}
