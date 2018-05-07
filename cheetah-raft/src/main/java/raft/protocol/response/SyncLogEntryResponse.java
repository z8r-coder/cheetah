package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-05-07
 * @desc
 */
public class SyncLogEntryResponse extends RaftResponse {

    private long lastLogIndex;

    public SyncLogEntryResponse(long serverId) {
        super(serverId);
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }
}
