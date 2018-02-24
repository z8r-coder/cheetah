package raft.core.server;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc
 */
public class RaftServer {

    public enum NodeState {
        FOLLOWER,
        CANDIDATE,
        LEADER;
    }

    private int host;

    private int port;

    private NodeState serverState;

    private int serverId;

    RaftServer (int host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerState(NodeState serverState) {
        this.serverState = serverState;
    }

    public NodeState getServerState() {
        return serverState;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }
}
