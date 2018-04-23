package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class SetKVResponse extends RaftResponse{

    private String respMessage;

    public SetKVResponse(int serverId, String respMessage) {
        super(serverId);
        this.respMessage = respMessage;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }
}
