package raft.protocol.request;

import raft.model.BaseRequest;
import raft.protocol.RaftLog;

import java.io.Serializable;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-05-07
 * @desc
 */
public class SyncLogEntryRequest extends BaseRequest implements Serializable {
    private long serverId;
    private long leaderCommit;
    private List<RaftLog.LogEntry> logEntries;

    public SyncLogEntryRequest (long serverId, List<RaftLog.LogEntry> logEntries,
                                long leaderCommit) {
        this.serverId = serverId;
        this.logEntries = logEntries;
        this.leaderCommit = leaderCommit;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public List<RaftLog.LogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<RaftLog.LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }
}
