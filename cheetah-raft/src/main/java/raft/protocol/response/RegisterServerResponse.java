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
    private long leaderId;
    private int currentTerm;

    public RegisterServerResponse (long serverId) {
        super(serverId);
    }

    public RegisterServerResponse(long serverId, Map<Long, String> serverList,
                                  Map<Long, ServerNode> serverNodeCache, long leaderId,
                                  int currentTerm) {
        super(serverId);
        this.serverList = serverList;
        this.serverNodeCache = serverNodeCache;
        this.leaderId = leaderId;
        this.currentTerm = currentTerm;
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

    public long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }
}
