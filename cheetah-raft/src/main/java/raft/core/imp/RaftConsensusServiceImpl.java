package raft.core.imp;

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

    public VotedResponse leaderElection(VotedRequest request) {
        raftNode.getLock().lock();
        try {
            VotedResponse votedResponse = new VotedResponse(raftNode.getCurrentTerm(),
                    false,
                    raftNode.getRaftServer().getServerId());
            if (request.getTerm() < raftNode.getCurrentTerm()) {
                return votedResponse;
            }
            if (request.getTerm() > raftNode.getCurrentTerm()) {
                raftCore.updateMore(request.getTerm());
            }
            boolean newLog = request.getLastLogTerm() >= raftNode.getRaftLog().getLastLogTerm()
                    && request.getLastLogIndex() >= raftNode.getRaftLog().getLastLogIndex();
            if ((raftNode.getVotedFor() == 0 || raftNode.getVotedFor() == request.getServerId()) &&
                    newLog) {
                raftNode.setVotedFor(request.getServerId());
                // TODO: 2018/3/29 need to update log 
                votedResponse.setGranted(true);
                votedResponse.setTerm(raftNode.getCurrentTerm());
                votedResponse.setServerId(raftNode.getRaftServer().getServerId());
            }
            return votedResponse;
        } finally {
            raftNode.getLock().unlock();
        }
    }

    public void resetTimeOut() {
        raftCore.resetElectionTimer();
    }

    public AddResponse appendEntries(AddRequest request) {
        return null;
    }
}
