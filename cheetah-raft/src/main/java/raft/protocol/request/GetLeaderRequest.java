package raft.protocol.request;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetLeaderRequest extends BaseRequest {

    private int serverId;

    public GetLeaderRequest () {

    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
