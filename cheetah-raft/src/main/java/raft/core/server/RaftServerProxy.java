package raft.core.server;

import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.core.imp.RaftConsensusServiceImpl;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import rpc.nio.AbstractRpcNioSelector;
import rpc.registry.ServerProxy;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-03-06
 * @desc
 */
public class RaftServerProxy extends ServerProxy {
    private Logger logger = Logger.getLogger(RaftServerProxy.class);

    private RaftConsensusService raftConsensusService;

    public RaftServerProxy() {
        super();
    }

    public RaftServerProxy(AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector, configuration);
    }

    public void init() {

    }

    public void startService() {
        super.startService();
        RaftOptions raftOptions = new RaftOptions();
        RaftServer raftServer = new RaftServer(getHost(), getPort());
        RaftLog raftLog = new RaftLog(0, 0,0,0);
        RaftNode raftNode = new RaftNode(raftLog, raftServer);
        RaftCore raftCore = new RaftCore(raftOptions, raftNode, cacheServerList);
        raftConsensusService = new RaftConsensusServiceImpl(raftNode, raftCore);
    }

    public void stopService() {
        super.stopService();
    }
}
