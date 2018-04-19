package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetLeaderResponse extends RaftResponse {

    public int leaderId;

    public GetLeaderResponse(int serverId, int leaderId) {
        super(serverId);
        this.leaderId = leaderId;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }
}
