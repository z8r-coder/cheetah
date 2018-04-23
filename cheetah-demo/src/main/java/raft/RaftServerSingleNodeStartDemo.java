package raft;


import raft.core.server.RaftClusterServer;

/**
 * @author ruanxin
 * @create 2018-04-14
 * @desc 单点多线程DEMO
 */
public class RaftServerSingleNodeStartDemo {
    public static void main(String[] args) {
        RaftClusterServer clusterServer = RaftClusterServer.getRaftClusterServer();
        clusterServer.startServerNode();
    }
}
