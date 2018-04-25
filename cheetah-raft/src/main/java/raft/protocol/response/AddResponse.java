package raft.protocol.response;

/**
 * @author ruanxin
 * @create 2018-03-30
 * @desc
 */
public class AddResponse extends RaftResponse {
    //当前的任期号，用于领导人去更新自己
    private int term;
    //跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真
    private boolean success;
    //用于leader和follower日志不一致时，找到最近一条不一致的日志
    private long lastLogIndex;

    public AddResponse(long serverId, int term, boolean success) {
        super(serverId);
        this.term = term;
        this.success = success;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }
}
