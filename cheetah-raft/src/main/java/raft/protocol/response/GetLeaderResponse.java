package raft.protocol.response;

import models.CheetahAddress;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetLeaderResponse extends RaftResponse {

    private long leaderId;
    private CheetahAddress cheetahAddress;

    public GetLeaderResponse(long serverId, long leaderId, CheetahAddress cheetahAddress) {
        super(serverId);
        this.leaderId = leaderId;
        this.cheetahAddress = cheetahAddress;
    }

    public long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }

    public CheetahAddress getCheetahAddress() {
        return cheetahAddress;
    }

    public void setCheetahAddress(CheetahAddress cheetahAddress) {
        this.cheetahAddress = cheetahAddress;
    }
}
