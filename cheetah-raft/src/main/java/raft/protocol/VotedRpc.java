package raft.protocol;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 请求投票的RPC
 */
public class VotedRpc {
    //候选人的任期号
    private int term;
    //请求选票的候选人的 Id
    private int candidateId;
    //候选人的最后日志条目的索引值
    private long lastLogIndex;
    //候选人最后日志条目的任期号
    private int lastLogTerm;

    public VotedRpc (int term, int candidateId, int lastLogIndex, int lastLogTerm) {
        this.term = term;
        this.candidateId = candidateId;
        this.lastLogIndex = lastLogIndex;
        this.lastLogTerm = lastLogTerm;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }
}
