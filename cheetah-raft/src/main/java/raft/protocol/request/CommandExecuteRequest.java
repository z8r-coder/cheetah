package raft.protocol.request;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class CommandExecuteRequest extends BaseRequest {

    private String command;
    private int serverId;

    public CommandExecuteRequest (String command) {
        this.command = command;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
