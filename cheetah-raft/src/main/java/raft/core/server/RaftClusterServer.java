package raft.core.server;

import mock.RaftMock;
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

    public Map<Long, CheetahServer> cacheRaftServer = new HashMap<>();

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
            RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(RaftMock.rootPathMapping.get(cheetahAddress.getPort()),
                    cheetahAddress.getHost(),
                    cheetahAddress.getPort());
            CheetahServer cheetahServer = new CheetahServer(acceptor);
            //init cluster
            cheetahServer.clusterInit();
            cacheRaftServer.put(ParseUtils.generateServerId(cheetahAddress.getHost(),
                    cheetahAddress.getPort()), cheetahServer);
        }
    }

    public void stopServerNode() {
        for (Map.Entry<Long, CheetahServer> entry : cacheRaftServer.entrySet()) {
            CheetahServer cheetahServer = entry.getValue();
            if (cheetahServer != null) {
                cheetahServer.stop();
            }
        }
    }

    public void stopOneServerNode (String serverId) {
        CheetahServer cheetahServer = cacheRaftServer.get(serverId);
        cheetahServer.stop();
    }
}
