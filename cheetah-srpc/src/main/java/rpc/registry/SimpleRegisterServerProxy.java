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
public class SimpleRegisterServerProxy implements RpcService {

    private final static Logger logger = Logger.getLogger(SimpleRegisterServerProxy.class);

    private SimpleRegisterServer registerServer;
    private Configuration configuration;
    private ServerRegisterInfo registerInfo;

    public SimpleRegisterServerProxy () {
        this.registerInfo = new ServerRegisterInfo();
        this.registerServer = new SimpleRegisterServer(registerInfo);
        this.configuration = new Configuration();
    }
    public SimpleRegisterServerProxy (SimpleRegisterServer registerServer,
                                Configuration configuration) {
        this.registerServer = registerServer;
        this.configuration = configuration;
    }

    public void startService() {

        String host = configuration.getRegisterHost();
        int port = configuration.getRegisterPort();
        registerServer.setHost(host);
        registerServer.setPort(port);
        SimpleServerRemoteExecutor remoteExecutor = new SimpleServerRemoteExecutor();

        RpcServiceProvider provider = new RpcServiceProvider(remoteExecutor);
        remoteExecutor.registerRemote(IServerRegisterInfo.class, registerInfo);

        registerServer.addRpcCallListener(provider);

        registerServer.startService();

        logger.info("Register Center has registered!");
    }

    public void stopService() {
        registerServer.stopService();
    }
}
