package raft.core.imp;

import org.apache.log4j.Logger;
import raft.core.RaftConsensusService;
import raft.protocol.*;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc
 */
public class RaftConsensusServiceImpl implements RaftConsensusService {

    private Logger logger = Logger.getLogger(RaftConsensusServiceImpl.class);

    private RaftNode raftNode;

    public RaftConsensusServiceImpl (RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    public RaftResponse leaderElection(VotedRequest request) {
        raftNode.getLock().lock();
        try {
            RaftResponse raftResponse = new RaftResponse(raftNode.getCurrentTerm(), false);
            if (raftNode.getCurrentTerm() > request.getTerm()) {
                return raftResponse;
            }
            if (raftNode.getVotedFor() == 0 ||
                    raftNode.getVotedFor() == request.getCandidateId()) {

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
