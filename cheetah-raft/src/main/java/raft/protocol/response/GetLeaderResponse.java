package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetLeaderResponse extends RaftResponse {

    private long leaderId;

    public GetLeaderResponse(long serverId, long leaderId) {
        super(serverId);
        this.leaderId = leaderId;
    }

    public long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }
}
