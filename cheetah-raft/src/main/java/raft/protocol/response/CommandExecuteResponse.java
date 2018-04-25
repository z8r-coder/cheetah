package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class CommandExecuteResponse extends RaftResponse {

    private String commandRtr;

    public CommandExecuteResponse (long serverId) {
        super(serverId);
    }

    public CommandExecuteResponse(long serverId, String commandRtr) {
        super(serverId);
        this.commandRtr = commandRtr;
    }

    public String getCommandRtr() {
        return commandRtr;
    }

    public void setCommandRtr(String commandRtr) {
        this.commandRtr = commandRtr;
    }
}
