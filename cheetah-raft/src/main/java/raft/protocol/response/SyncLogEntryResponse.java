package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-05-07
 * @desc
 */
public class SyncLogEntryResponse extends RaftResponse {
    private boolean success;
    private long lastLogIndex;

    public SyncLogEntryResponse(long serverId, boolean success) {
        super(serverId);
        this.success = success;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
