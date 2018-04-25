package raft.protocol.request;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class SetKVRequest extends BaseRequest implements Serializable {

    private String setCommand;
    private long serverId;

    public SetKVRequest (String setCommand) {
        this.setCommand = setCommand;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, long serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public String getSetCommand() {
        return setCommand;
    }

    public void setSetCommand(String setCommand) {
        this.setCommand = setCommand;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
