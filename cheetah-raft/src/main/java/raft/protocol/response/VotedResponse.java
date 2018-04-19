package raft.protocol.response;

import raft.protocol.response.RaftResponse;

/**
 * @author ruanxin
 * @create 2018-03-30
 * @desc
 */
public class VotedResponse extends RaftResponse {

    /**
     * concurrent term
     */
    private int term;
    /**
     * vote success
     */
    private boolean granted;

    public VotedResponse(int term, boolean granted, int serverId) {
        super(serverId);
        this.granted = granted;
        this.term = term;
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
