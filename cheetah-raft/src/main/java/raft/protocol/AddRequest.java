package raft.protocol;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 附加日志
 */
public class AddRequest extends BaseRequest implements Serializable {
    //领导人的任期号
    private int term;
    //领导人的 Id，以便于跟随者重定向请求
    private int leaderId;
    //准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率）
    private RaftLog raftLog;
//    //新的日志条目紧随之前的索引值
//    private long prevLogIndex;
//    //prevLogIndex 条目的任期号
//    private int prevLogTerm;
    //领导人已经提交的日志的索引值
//    private long leaderCommit;

    public AddRequest (int term, int leaderId, RaftLog raftLog) {
        this.term = term;
        this.leaderId = leaderId;
        this.raftLog = raftLog;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public RaftLog getRaftLog() {
        return raftLog;
    }

    public void setRaftLog(RaftLog raftLog) {
        this.raftLog = raftLog;
    }
}
