package raft.protocol.request;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetLeaderRequest extends BaseRequest implements Serializable {

    private long serverId;

    public GetLeaderRequest () {

    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, long serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
