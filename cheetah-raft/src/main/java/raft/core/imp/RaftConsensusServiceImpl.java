package raft.core.imp;

import org.apache.log4j.Logger;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.core.server.RaftServer;
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
        raftNode.getLock().lock();
        try {
            RaftServer raftServer = raftNode.getRaftServer();
            AddResponse response = new AddResponse(raftServer.getServerId(),
                    raftNode.getCurrentTerm(),
                    false);
            if (request.getTerm() < raftNode.getCurrentTerm()) {
                return response;
            }
            if (request.getTerm() > raftNode.getCurrentTerm()) {
                raftCore.updateMore(request.getTerm());
            }
            if (raftNode.getLeaderId() == 0) {
                raftNode.setLeaderId(request.getLeaderId());
                logger.info("new leaderId:" + raftNode.getLeaderId());
            }
            if (raftNode.getLeaderId() != request.getLeaderId()) {
                logger.warn("another server declare it is leader:" + raftNode.getLeaderId() +
                "at term:" + raftNode.getCurrentTerm() + "now real leader is " + request.getLeaderId() +
                "and the term will plus one!");
                raftNode.setLeaderId(request.getLeaderId());
                raftCore.updateMore(request.getTerm() + 1);
                response.setSuccess(false);
                response.setTerm(request.getTerm() + 1);
                response.setServerId(raftServer.getServerId());
                return response;
            }

        } finally {
            raftNode.getLock().unlock();
        }
        return null;
    }
}
