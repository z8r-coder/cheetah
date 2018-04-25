package raft.protocol.response;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetServerListResponse extends RaftResponse {
    private Map<Long, String> serverList;

    public GetServerListResponse(Map<Long, String> serverList, long serverId) {
        super(serverId);
        this.serverList = serverList;
    }

    public Map<Long, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Long, String> serverList) {
        this.serverList = serverList;
    }
}
