package raft.protocol.request;

import raft.model.BaseRequest;
import raft.protocol.RaftLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 附加日志
 */
public class AddRequest extends BaseRequest implements Serializable {
    //领导人的任期号
    private int term;
    //领导人的 Id，以便于跟随者重定向请求
    private long leaderId;
    //新的日志条目紧随之前的索引值, from -1
    private long prevLogIndex;
    //prevLogIndex 条目的任期号
    private int prevLogTerm;
    //领导人已经提交的日志的索引值
    private long leaderCommit;
    //log entry
    private List<RaftLog.LogEntry> logEntries;

    private long serverId;

    public AddRequest (int term, long leaderId, long prevLogIndex,
                       int prevLogTerm, long leaderCommit) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.leaderCommit = leaderCommit;
        logEntries = new ArrayList<>();
    }
    public AddRequest (int term, long leaderId, long prevLogIndex,
                       int prevLogTerm, long leaderCommit, List<RaftLog.LogEntry> logEntries) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.leaderCommit = leaderCommit;
        this.logEntries = logEntries;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, long serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }


    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }

    public List<RaftLog.LogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<RaftLog.LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
