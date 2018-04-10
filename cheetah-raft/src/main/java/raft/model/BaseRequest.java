package raft.model;

/**
 * @author ruanxin
 * @create 2018-03-06
 * @desc
 */
public class BaseRequest {
    private String localHost;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private int serverId;

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }
    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
