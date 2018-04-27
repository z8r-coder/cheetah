package raft.protocol.response;

import models.CheetahAddress;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetServerListResponse extends RaftResponse {
    private Map<Long, String> serverList;
    private CheetahAddress leaderAddress;

    public GetServerListResponse(Map<Long, String> serverList, long serverId, CheetahAddress leaderAddress) {
        super(serverId);
        this.serverList = serverList;
        this.leaderAddress = leaderAddress;
    }

    public Map<Long, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Long, String> serverList) {
        this.serverList = serverList;
    }

    public CheetahAddress getLeaderAddress() {
        return leaderAddress;
    }

    public void setLeaderAddress(CheetahAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }
}
