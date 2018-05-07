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
    private List<RaftLog.LogEntry> logEntries;

    public SyncLogEntryRequest (long serverId, List<RaftLog.LogEntry> logEntries) {
        this.serverId = serverId;
        this.logEntries = logEntries;
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
}
