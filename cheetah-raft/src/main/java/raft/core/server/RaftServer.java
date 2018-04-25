package raft.core.server;

import utils.ParseUtils;

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

    private String host;

    private int port;

    private NodeState serverState;

    private long serverId;

    public RaftServer (String host, int port) {
        this.host = host;
        this.port = port;
        serverId = ParseUtils.generateServerId(host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
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

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getServerId() {
        return serverId;
    }

    public static void main(String[] args) {
        String host = "192.0.0.1";
        int port = 8854;
        String preId = host.replaceAll("\\.","");
        int predId = Integer.parseInt(preId);
        String por = preId + port;
        int serverId = Integer.parseInt(por);
        System.out.println(serverId);
    }
}
