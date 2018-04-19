package raft.protocol.response;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class GetServerListResponse extends RaftResponse {
    private Map<Integer, String> serverList;

    public GetServerListResponse(Map<Integer, String> serverList, int serverId) {
        super(serverId);
        this.serverList = serverList;
    }

    public Map<Integer, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Integer, String> serverList) {
        this.serverList = serverList;
    }
}
