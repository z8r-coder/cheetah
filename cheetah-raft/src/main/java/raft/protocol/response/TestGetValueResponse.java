package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-05-11
 * @desc
 */
public class TestGetValueResponse extends RaftResponse {

    private String value;

    public TestGetValueResponse(long serverId, String value) {
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
