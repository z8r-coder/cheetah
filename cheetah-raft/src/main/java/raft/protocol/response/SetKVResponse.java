package raft.protocol.response;

import models.CheetahAddress;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class SetKVResponse extends RaftResponse{

    private String respMessage;
    private CheetahAddress leaderAddress;

    public SetKVResponse(long serverId, CheetahAddress leaderAddress) {
        super(serverId);
        this.leaderAddress = leaderAddress;
    }

    public SetKVResponse(long serverId, String respMessage) {
        super(serverId);
        this.respMessage = respMessage;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public CheetahAddress getLeaderAddress() {
        return leaderAddress;
    }

    public void setLeaderAddress(CheetahAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }
}
