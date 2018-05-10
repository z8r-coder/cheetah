package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-05-07
 * @desc
 */
public class SyncLogEntryResponse extends RaftResponse {
    private int syncStatus;
    private long lastLogIndex;

    public SyncLogEntryResponse(long serverId, int syncStatus) {
        super(serverId);
        this.syncStatus = syncStatus;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
