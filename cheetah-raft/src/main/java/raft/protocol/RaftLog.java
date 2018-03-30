package raft.protocol;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc 日志
 */
public class RaftLog {

    private Logger log = Logger.getLogger(RaftLog.class);

    //候选人最后日志条目的任期号
    private int lastLogTerm;
    //候选人的最后日志条目的索引值
    private long lastLogIndex;
    //已知的最大的已经被提交的日志条目的索引值
    private long commitIndex;
    //最后被应用到状态机的日志条目索引值
    private long lastApplied = 0;
    //log entry
    private Map<Long, LogEntry> logEntries = new TreeMap<Long, LogEntry>();

    public static class LogEntry {
        private long term;
        private long index;
        private byte[] data;

        public LogEntry() {
            term = 0l;
            index = 0l;
            data = new byte[1];
        }
        public LogEntry(long term, long index, byte[] data) {
            this.data = data;
            this.term = term;
            this.index = index;
        }

        public long getTerm() {
            return term;
        }

        public void setTerm(long term) {
            this.term = term;
        }

        public long getIndex() {
            return index;
        }

        public void setIndex(long index) {
            this.index = index;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
    public RaftLog(long commitIndex, long lastApplied, int lastLogTerm, int lastLogIndex) {
        this.commitIndex = commitIndex;
        this.lastApplied = lastApplied;
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
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

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }
}
