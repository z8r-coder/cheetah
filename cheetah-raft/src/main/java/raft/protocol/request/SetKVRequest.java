package raft.protocol.request;

import raft.model.BaseRequest;
import utils.DateUtil;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class SetKVRequest extends BaseRequest {

    private String setCommand;
    private int serverId;

    public SetKVRequest (String setCommand) {
        this.setCommand = setCommand;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
