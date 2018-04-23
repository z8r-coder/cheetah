package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class CommandParseResponse extends RaftResponse{

    private String result;

    public CommandParseResponse(int serverId, String result) {
        super(serverId);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
