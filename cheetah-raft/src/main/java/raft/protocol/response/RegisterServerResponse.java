package raft.protocol.response;

import raft.core.server.ServerNode;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-05-01
 * @desc
 */
public class RegisterServerResponse extends RaftResponse {

    private Map<Long, String> serverList;
    private Map<Long, ServerNode> serverNodeCache;

    public RegisterServerResponse (long serverId) {
        super(serverId);
    }

    public RegisterServerResponse(long serverId, Map<Long, String> serverList,
                                  Map<Long, ServerNode> serverNodeCache) {
        super(serverId);
        this.serverList = serverList;
        this.serverNodeCache = serverNodeCache;
    }

    public Map<Long, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Long, String> serverList) {
        this.serverList = serverList;
    }

    public Map<Long, ServerNode> getServerNodeCache() {
        return serverNodeCache;
    }

    public void setServerNodeCache(Map<Long, ServerNode> serverNodeCache) {
        this.serverNodeCache = serverNodeCache;
    }
}
