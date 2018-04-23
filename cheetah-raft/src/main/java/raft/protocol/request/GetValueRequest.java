package raft.protocol.request;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class GetValueRequest extends BaseRequest {
    private String key;
    //direct log
    private int serverId;

    public GetValueRequest (String key) {
        this.key = key;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
