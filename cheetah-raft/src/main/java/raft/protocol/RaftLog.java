package raft.protocol;

import org.apache.log4j.Logger;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc 日志
 */
public class RaftLog {

    private Logger log = Logger.getLogger(RaftLog.class);

    //候选人最后日志条目的任期号
    private long lastLogTerm;
    //候选人的最后日志条目的索引值
    private long lastLogIndex;
    //已知的最大的已经被提交的日志条目的索引值
    private long commitIndex;
    //最后被应用到状态机的日志条目索引值
    private long lastApplied = 0;

    public RaftLog(long commitIndex, long lastApplied) {
        this.commitIndex = commitIndex;
        this.lastApplied = lastApplied;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public long getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(long lastApplied) {
        this.lastApplied = lastApplied;
    }

    public long getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(long lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }
}
