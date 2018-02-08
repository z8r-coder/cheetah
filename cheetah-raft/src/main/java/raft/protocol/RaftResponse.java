package raft.protocol;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 返回
 */
public class RaftResponse {
    //当前任期号，以便于候选人去更新自己的任期号
    private int term;
    //候选人赢得了此张选票时为真
    private boolean granted;

    public RaftResponse (int term, boolean granted) {
        this.term = term;
        this.granted = granted;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }
}
