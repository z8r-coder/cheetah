package raft;

import raft.core.server.RaftClusterServer;

/**
 * @author ruanxin
 * @create 2018-04-14
 * @desc 单点
 */
public class RaftServerSingleNodeDemo {
    public static void main(String[] args) {
        String servers = args[0];
        RaftClusterServer clusterServer = new RaftClusterServer();
        clusterServer.startServerNode(servers);
    }
}
