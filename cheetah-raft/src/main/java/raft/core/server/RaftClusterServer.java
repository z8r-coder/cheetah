package raft.core.server;

import models.CheetahAddress;
import raft.core.rpc.RaftRpcServerAcceptor;
import utils.Configuration;
import utils.ParseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-14
 * @desc cluster solution single node, run more than one process
 */
public class RaftClusterServer {

    public Map<Integer, RaftRpcServerAcceptor> cacheRaftServer = new HashMap<>();

    private final static RaftClusterServer raftClusterServer = new RaftClusterServer();

    private RaftClusterServer () {

    }

    public static RaftClusterServer getRaftClusterServer() {
        return raftClusterServer;
    }

    public void startServerNode() {
        Configuration configuration = new Configuration();
        String servers = configuration.getRaftInitServer();
        List<CheetahAddress> addresses = ParseUtils.parseCommandAddress(servers);
        for (CheetahAddress cheetahAddress : addresses) {
            RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(cheetahAddress.getHost(),
                    cheetahAddress.getPort());
            acceptor.startService();
            cacheRaftServer.put(ParseUtils.generateServerId(cheetahAddress.getHost(),
                    cheetahAddress.getPort()), acceptor);
        }
    }

    public void stopServerNode() {
        for (Map.Entry<Integer, RaftRpcServerAcceptor> entry : cacheRaftServer.entrySet()) {
            RaftRpcServerAcceptor acceptor = entry.getValue();
            if (acceptor != null) {
                acceptor.stopService();
            }
        }
    }
}
