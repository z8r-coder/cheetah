package raft.core.rpc;

import models.CheetahAddress;
import raft.constants.RaftOptions;
import raft.core.RaftAsyncConsensusService;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.core.StateMachine;
import raft.core.imp.RaftAsyncConsensusServiceImpl;
import raft.core.imp.RaftConsensusServiceImpl;
import raft.core.server.RaftServer;
import raft.demo.statemachine.ExampleStateMachine;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import rpc.wrapper.RpcAcceptorWrapper;
import utils.Configuration;
import utils.ParseUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc raft rpc acceptor
 */
public class RaftRpcServerAcceptor extends RpcAcceptorWrapper {
    private Configuration configuration;

    public RaftRpcServerAcceptor(String host, int port) {
        super(host, port);
        this.configuration = new Configuration();
    }

    @Override
    public void register() {
        RaftOptions raftOptions = new RaftOptions();
        RaftServer raftServer = new RaftServer(getHost(), getPort());

        RaftLog raftLog = new RaftLog(raftOptions.getMaxLogSizePerFile(), configuration.getRaftRootPath(), "raft_meta");
        StateMachine stateMachine = new ExampleStateMachine();
        RaftNode raftNode = new RaftNode(raftLog, raftServer, stateMachine);
        RaftCore raftCore = new RaftCore(raftOptions, raftNode, getCacheServerList());

        RaftConsensusServiceImpl raftConsensusService = new RaftConsensusServiceImpl(raftNode, raftCore);
        RaftAsyncConsensusServiceImpl raftAsyncConsensusService = new RaftAsyncConsensusServiceImpl(raftNode, raftCore);

        remoteExecutor.registerRemote(RaftConsensusService.class, raftConsensusService);
        remoteExecutor.registerRemote(RaftAsyncConsensusService.class, raftAsyncConsensusService);

        //todo test
//        HelloRpcServiceImpl helloRpcService = new HelloRpcServiceImpl();
//        remoteExecutor.registerRemote(HelloRpcService.class, helloRpcService);
    }

    /**
     * 解析数据
     * @return
     */
    private Map<Integer, String> getCacheServerList () {
        Map<Integer, String> cacheServerList = new ConcurrentHashMap<>();
        String raftInitServers = configuration.getRaftInitServer();
        List<CheetahAddress> addresses = ParseUtils.parseCommandAddress(raftInitServers);
        for (CheetahAddress cheetahAddress : addresses) {
            int serverId = ParseUtils.generateServerId(cheetahAddress.getHost(), cheetahAddress.getPort());
            String server = ParseUtils.generateServerIp(cheetahAddress.getHost(), cheetahAddress.getPort());
            cacheServerList.put(serverId, server);
        }
        return cacheServerList;
    }
}
