package raft.core.server;

import models.CheetahAddress;
import rpc.registry.AbstractServerProxy;
import rpc.registry.SimpleRegisterServer;
import rpc.registry.SimpleRegisterServerProxy;
import rpc.registry.SimpleServerProxy;
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

    public Map<Integer, RaftServerDelegate> cacheRaftServer = new HashMap<>();

    public void startRegister () {
        SimpleRegisterServerProxy registerServerProxy = new SimpleRegisterServerProxy();
        registerServerProxy.startService();
    }

    public void startServerNode(String servers) {
        startRegister();
        List<CheetahAddress> addresses = ParseUtils.parseCommandAddress(servers);
        for (CheetahAddress cheetahAddress : addresses) {
            AbstractServerProxy serverProxy = new SimpleServerProxy(cheetahAddress.getHost(), cheetahAddress.getPort());
            serverProxy.setCacheServerList(addresses);
            RaftServerDelegate raftServerDelegate = new RaftServerDelegate(serverProxy);
            raftServerDelegate.startService();
            cacheRaftServer.put(ParseUtils.generateServerId(cheetahAddress.getHost(),
                    cheetahAddress.getPort()), raftServerDelegate);
        }
    }

    public void stopServerNode() {
        for (Integer serverId : cacheRaftServer.keySet()) {
            RaftServerDelegate raftServerDelegate = cacheRaftServer.get(serverId);
            if (raftServerDelegate != null) {
                raftServerDelegate.stopService();
            }
        }
    }
}
