package raft.core.rpc;

import raft.constants.RaftOptions;
import raft.core.*;
import raft.core.imp.RaftAsyncConsensusServiceImpl;
import raft.core.imp.RaftConsensusServiceImpl;
import raft.core.server.RaftServer;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import raft.utils.RaftUtils;
import rpc.wrapper.RpcAcceptorWrapper;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-04-17
 * @desc raft rpc acceptor
 */
public class RaftRpcServerAcceptor extends RpcAcceptorWrapper {
    private Configuration configuration;
    private RaftNode raftNode;
    private RaftCore raftCore;

    public RaftRpcServerAcceptor(String host, int port) {
        super(host, port);
        this.configuration = new Configuration();
    }

    @Override
    public void register() {
        RaftOptions raftOptions = new RaftOptions();
        RaftServer raftServer = new RaftServer(getHost(), getPort());

        RaftLog raftLog = new RaftLog(raftOptions.getMaxLogSizePerFile(), configuration.getRaftRootPath(), "raft_meta");
        //state machine
        StateMachine stateMachine = new CheetahStateMachine();
        raftNode = new RaftNode(raftLog, raftServer, stateMachine);
        raftCore = new RaftCore(raftOptions, raftNode, RaftUtils.getInitCacheServerList(configuration));

        RaftConsensusServiceImpl raftConsensusService = new RaftConsensusServiceImpl(raftNode, raftCore);
        RaftAsyncConsensusServiceImpl raftAsyncConsensusService = new RaftAsyncConsensusServiceImpl(raftNode, raftCore);

        remoteExecutor.registerRemote(RaftConsensusService.class, raftConsensusService);
        remoteExecutor.registerRemote(RaftAsyncConsensusService.class, raftAsyncConsensusService);
    }

    public RaftNode getRaftNode() {
        return raftNode;
    }

    public void setRaftNode(RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    public RaftCore getRaftCore() {
        return raftCore;
    }

    public void setRaftCore(RaftCore raftCore) {
        this.raftCore = raftCore;
    }
}
