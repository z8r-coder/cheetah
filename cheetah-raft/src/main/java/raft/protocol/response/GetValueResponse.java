package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class GetValueResponse extends RaftResponse {

    private String value;

    public GetValueResponse(int serverId, String value) {
        super(serverId);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
