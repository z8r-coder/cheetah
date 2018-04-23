package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class CommandParseResponse{

    private String result;

    public CommandParseResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
