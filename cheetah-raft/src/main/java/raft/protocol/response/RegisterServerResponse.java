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
    private long leaderId;
    private int currentTerm;
    private boolean successful;

    public RegisterServerResponse (long serverId) {
        super(serverId);
    }

    public RegisterServerResponse(long serverId, Map<Long, String> serverList,
                                  long leaderId, int currentTerm, boolean successful) {
        super(serverId);
        this.serverList = serverList;
        this.leaderId = leaderId;
        this.currentTerm = currentTerm;
        this.successful = successful;
    }

    public Map<Long, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Long, String> serverList) {
        this.serverList = serverList;
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

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
