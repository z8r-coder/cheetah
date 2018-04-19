package raft;

import raft.core.server.RaftClusterServer;

/**
 * @author ruanxin
 * @create 2018-04-16
 * @desc 单点多线程DEMO
 */
public class RaftServerSingleNodeStopDemo {
    public static void main(String[] args) {
        RaftClusterServer raftClusterServer = RaftClusterServer.getRaftClusterServer();
        raftClusterServer.stopServerNode();
    }
}
