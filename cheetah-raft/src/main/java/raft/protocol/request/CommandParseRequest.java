package raft.protocol.request;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class CommandParseRequest extends BaseRequest {
    private String command;

    public CommandParseRequest (String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
