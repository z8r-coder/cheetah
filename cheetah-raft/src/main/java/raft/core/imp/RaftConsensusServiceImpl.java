package raft.core.imp;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.log4j.Logger;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.protocol.*;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc
 */
public class RaftConsensusServiceImpl implements RaftConsensusService {

    private Logger logger = Logger.getLogger(RaftConsensusServiceImpl.class);

    private RaftNode raftNode;
    private RaftCore raftCore;

    public RaftConsensusServiceImpl (RaftNode raftNode, RaftCore raftCore) {
        this.raftNode = raftNode;
        this.raftCore = raftCore;
    }

    public RaftResponse leaderElection(VotedRequest request) {
        raftNode.getLock().lock();
        try {
            RaftResponse raftResponse = new RaftResponse(raftNode.getCurrentTerm(), false);
            if (request.getTerm() < raftNode.getCurrentTerm()) {
                return raftResponse;
            }
            if (request.getTerm() > raftNode.getCurrentTerm()) {
                raftCore.updateMore(request.getTerm());
            }
            boolean newLog = false;
        } finally {
            raftNode.getLock().unlock();
        }
        return null;
    }

    public RaftResponse appendEntry(AddRequest request) {
        return null;
    }
}
