package raft.core.imp;

import org.apache.log4j.Logger;
import raft.core.RaftAsyncConsensusService;
import raft.core.RaftCore;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import raft.protocol.request.SyncLogEntryRequest;
import raft.protocol.request.VotedRequest;
import raft.protocol.response.SyncLogEntryResponse;
import raft.protocol.response.VotedResponse;

import java.util.List;

/**
 * @author ruanxin
 * @create 2018-04-10
 * @desc
 */
public class RaftAsyncConsensusServiceImpl implements RaftAsyncConsensusService {
    private Logger logger = Logger.getLogger(RaftAsyncConsensusServiceImpl.class);

    private RaftNode raftNode;
    private RaftCore raftCore;

    public RaftAsyncConsensusServiceImpl (RaftNode raftNode, RaftCore raftCore) {
        this.raftNode = raftNode;
        this.raftCore = raftCore;
    }

    public VotedResponse leaderElection(VotedRequest request) {
        raftNode.getLock().lock();
        try {
            VotedResponse votedResponse = new VotedResponse(raftNode.getCurrentTerm(),
                    false,
                    raftNode.getRaftServer().getServerId());
            if (request == null) {
                return votedResponse;
            }
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
                votedResponse.setGranted(true);
                votedResponse.setTerm(raftNode.getCurrentTerm());
                votedResponse.setServerId(raftNode.getRaftServer().getServerId());
            }
            return votedResponse;
        } finally {
            raftNode.getLock().unlock();
        }
    }

    @Override
    public SyncLogEntryResponse syncLogEntry(SyncLogEntryRequest request) {
        logger.info("sync log entries info begin from serverId=" + request.getServerId());
        SyncLogEntryResponse response = new SyncLogEntryResponse(raftNode.getRaftServer().getServerId(),
                false);
        List<RaftLog.LogEntry> entries = request.getLogEntries();
        RaftLog.LogEntry firstNeedSyncEntry = entries.get(0);
        if (firstNeedSyncEntry.getIndex() != raftNode.getRaftLog().getLastLogIndex() + 1) {
            logger.warn("syncLogEntry sync entry can't match!");
            response.setLastLogIndex(raftNode.getRaftLog().getLastLogIndex());
            return response;
        }

        return null;
    }
}
