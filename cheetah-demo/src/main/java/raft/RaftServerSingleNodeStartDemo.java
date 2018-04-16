package raft;

import raft.core.server.RaftClusterServer;

/**
 * @author ruanxin
 * @create 2018-04-14
 * @desc 单点
 */
public class RaftServerSingleNodeStartDemo {
    public static void main(String[] args) {
        String servers = null;
        if (args.length == 0) {
            servers = "127.0.0.1:7070,127.0.0.1:8080,127.0.0.1:6060";
        } else {
            servers = args[0];
        }
        RaftClusterServer clusterServer = RaftClusterServer.getRaftClusterServer();
        clusterServer.startServerNode(servers);
    }
}
