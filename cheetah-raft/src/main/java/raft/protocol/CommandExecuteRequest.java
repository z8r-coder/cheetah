package raft.protocol;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class CommandExecuteRequest extends BaseRequest {

    private String command;

    public CommandExecuteRequest (String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
