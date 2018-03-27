package raft.protocol;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 返回
 */
public class RaftResponse implements Serializable{
    //当前任期号，以便于候选人去更新自己的任期号
    private int term;
    //候选人赢得了此张选票时为真
    private boolean granted;
    //标注来自哪台服务器
    private int serverId;

    public RaftResponse (int term, boolean granted, int serverId) {
        this.term = term;
        this.granted = granted;
        this.serverId = serverId;
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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
