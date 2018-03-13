package raft.core.server;

import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.core.imp.RaftConsensusServiceImpl;
import raft.model.BaseRequest;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import rpc.Service;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.registry.AbstractServerProxy;
import rpc.server.RpcServiceProvider;
import rpc.server.SimpleServerRemoteExecutor;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-03-06
 * @desc
 */
public class RaftServerDelegate implements Service {

    private Logger logger = Logger.getLogger(RaftServer.class);

    private RaftConsensusService raftConsensusService;
    private BaseRequest request;

    private AbstractServerProxy delegateServer;

    public RaftServerDelegate(BaseRequest request, AbstractServerProxy delegateServer) {
        this.request = request;
        this.delegateServer = delegateServer;
    }

    public void init() {
        RaftOptions raftOptions = new RaftOptions();
        RaftServer raftServer = new RaftServer(delegateServer.getHost(), delegateServer.getPort());
        RaftLog raftLog = new RaftLog(0, 0,0,0);
        RaftNode raftNode = new RaftNode(raftLog, raftServer);
        RaftCore raftCore = new RaftCore(raftOptions, raftNode, delegateServer.getCacheServerList());
        raftConsensusService = new RaftConsensusServiceImpl(raftNode, raftCore);
    }

    public void startService() {
        register();
    }

    private void register() {
        init();
        //def server accept
        SimpleServerRemoteExecutor remoteExecutor = new SimpleServerRemoteExecutor();
        RpcServiceProvider provider = new RpcServiceProvider(remoteExecutor);
        remoteExecutor.registerRemote(RaftConsensusService.class, raftConsensusService);

        delegateServer.addRpcCallListener(provider);
        delegateServer.startService();

//        //def client connect
//        AbstractRpcConnector connector = new RpcNioConnector(null);
//        RpcUtils.setAddress(request.getRemoteHost(), request.getRemotePort(), connector);
//        SimpleClientRemoteExecutor clientRemoteExecutor = new SimpleClientRemoteExecutor(connector);
//        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
//        proxy.startService();
    }

    public void stopService() {
        delegateServer.stopService();
    }
}
