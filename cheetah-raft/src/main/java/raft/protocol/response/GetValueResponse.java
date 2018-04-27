package raft.protocol.response;

import models.CheetahAddress;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class GetValueResponse extends RaftResponse {

    private String value;
    private CheetahAddress leaderAddress;

    public GetValueResponse(long serverId, CheetahAddress leaderAddress) {
        super(serverId);
        this.leaderAddress = leaderAddress;
    }

    public GetValueResponse(long serverId, String value) {
        super(serverId);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CheetahAddress getLeaderAddress() {
        return leaderAddress;
    }

    public void setLeaderAddress(CheetahAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }
}
