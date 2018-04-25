package raft.protocol.request;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class GetValueRequest extends BaseRequest implements Serializable {
    private String key;
    //direct log
    private long serverId;

    public GetValueRequest (String key) {
        this.key = key;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, long serverId) {
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

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
