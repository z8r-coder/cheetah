package raft.core.imp;

import org.apache.log4j.Logger;
import raft.core.RaftConsensusService;
import raft.core.RaftCore;
import raft.core.server.RaftServer;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import raft.protocol.request.*;
import raft.protocol.response.*;

import java.util.ArrayList;
import java.util.List;

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
        // -> async
        System.out.println("test leader election!");
        return null;
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
            if (request == null) {
                logger.debug("appendEntries, but request=null");
                return response;
            }
            logger.info("begin appendEntries from server=" + request.getServerId() +
            " ,local server=" + raftNode.getRaftServer().getServerId());
            if (request.getTerm() < raftNode.getCurrentTerm()) {
                logger.info("remote server term=" + request.getTerm() +
                " ,local server term=" + raftNode.getCurrentTerm() +
                " ,so return!");
                return response;
            }
            if (request.getTerm() > raftNode.getCurrentTerm()) {
                logger.info("remote server term=" + request.getTerm() +
                " ,local server term=" + raftNode.getCurrentTerm() +
                " ,so update more!");
                raftCore.updateMore(request.getTerm());
            }
            if (raftNode.getLeaderId() == 0) {
                raftNode.setLeaderId(request.getLeaderId());
                logger.info("new leaderId:" + raftNode.getLeaderId());
            }
            if (raftNode.getLeaderId() != request.getLeaderId()) {
                logger.warn("another server declare it is leader:" + raftNode.getLeaderId() +
                " at term:" + raftNode.getCurrentTerm() + " ,now real leader is " + request.getLeaderId() +
                " and the term will plus one!");
                raftNode.setLeaderId(request.getLeaderId());
                raftCore.updateMore(request.getTerm() + 1);
                response.setSuccess(false);
                response.setTerm(request.getTerm() + 1);
                response.setServerId(raftServer.getServerId());
                return response;
            }
            if (request.getPrevLogIndex() > raftNode.getRaftLog().getLastLogIndex()) {
                logger.info("Refuse,request's log index:" + request.getPrevLogIndex() +
                "local server's log index:" + raftNode.getRaftLog().getLastLogIndex());
                return response;
            }
            if (request.getPrevLogTerm() != raftNode.getRaftLog().getLogEntryTerm(request.getPrevLogTerm())) {
                logger.warn("LogEntry term is wrong, leader prevLogIndex=" + request.getPrevLogIndex() +
                ",prevLogTerm=" + request.getPrevLogTerm() + "but local server prevLogIndex=" + request.getPrevLogIndex() +
                ",prevLogTerm=" + raftNode.getRaftLog().getLogEntryTerm(request.getPrevLogIndex()));
                //找到最初不一致的地方，然后覆盖掉
                response.setLastLogIndex(request.getPrevLogIndex() - 1);
                return response;
            }
            if (request.getLogEntries().size() == 0) {
                logger.info("heart beat request at term:" + request.getTerm() +
                " ,local host term:" + raftNode.getCurrentTerm() +
                " ,local serverId=" + raftServer.getServerId());
                response.setTerm(raftNode.getCurrentTerm());
                response.setServerId(raftServer.getServerId());
                response.setSuccess(true);
                response.setLastLogIndex(raftNode.getRaftLog().getLastLogIndex());
                //sync
                applyLogOnStateMachine(request);
                //reset election
                raftCore.resetElectionTimer();
                return response;
            }

            response.setSuccess(true);
            List<RaftLog.LogEntry> entries = new ArrayList<RaftLog.LogEntry>();
            long index = request.getPrevLogIndex();
            for (RaftLog.LogEntry entry : request.getLogEntries()) {
                index++;
                if (raftNode.getRaftLog().getLastLogIndex() > index) {
                    if (raftNode.getRaftLog().getLogEntryTerm(index) == entry.getTerm()) {
                        continue;
                    }
                    //truncate sync leader and follower
                    long lastIndexKept = index - 1;
                    raftNode.getRaftLog().truncateSuffix(lastIndexKept);
                }
                entries.add(entry);
            }
            raftNode.getRaftLog().append(entries);
            response.setLastLogIndex(raftNode.getRaftLog().getLastLogIndex());

            applyLogOnStateMachine(request);
            logger.info("Append entries request from server:" + request.getServerId() + " in term:" +
            request.getTerm() + " (my term is " + raftNode.getCurrentTerm() + ") " +
            " ,entryCount=" + request.getLogEntries().size() + " result:" + response.isSuccess());
            return response;
        } finally {
            raftNode.getLock().unlock();
        }
    }

    @Override
    public GetLeaderResponse getLeader(GetLeaderRequest request) {
        logger.info("local serverId=" + raftNode.getRaftServer().getServerId() +
        " ,remote host=" + request.getRemoteHost());
        GetLeaderResponse response = new GetLeaderResponse(raftNode.getRaftServer().getServerId(),
                raftNode.getLeaderId());
        return response;
    }

    @Override
    public GetServerListResponse getServerList(GetServerListRequest request) {
        logger.info("local serverId=" + raftNode.getRaftServer().getServerId() +
        " ,remote host=" + request.getRemoteHost());
        GetServerListResponse response = new GetServerListResponse(raftCore.getServerList(), raftNode.getRaftServer().getServerId());
        return response;
    }


    /**
     * only write
     * @param request
     * @return
     */
    @Override
    public CommandExecuteResponse clientCommandExec(CommandExecuteRequest request) {
        logger.info("local serverId=" + raftNode.getRaftServer().getServerId() +
        " ,remote host=" + request.getRemoteHost());
        CommandExecuteResponse response = new CommandExecuteResponse(raftNode.getRaftServer().getServerId());
        if (raftNode.getLeaderId() <= 0) {
            //have not election leader
            logger.info("there is no leader, local serverId=" + raftNode.getRaftServer().getServerId());
            response.setCommandRtr("no leader!");
            return response;
        } else if (raftNode.getRaftServer().getServerState() != RaftServer.NodeState.LEADER) {
            //重定向给leader
            logger.info("redirect to leader=" + raftNode.getLeaderId());
            CommandExecuteResponse resp = raftCore.redirectLeader(request);
            return resp;
        } else {
            //leader, do log Replication
            logger.info("local serverId=" + raftNode.getRaftServer().getServerId() +
            " begin do log replication");
            boolean result = raftCore.logReplication(request.getCommand().getBytes());
            if (result) {
                response.setCommandRtr("command execute successful!");
            } else {
                response.setCommandRtr("command execute fail!");
            }
            return response;
        }
    }

    //apply on state machine
    private void applyLogOnStateMachine(AddRequest request) {
        logger.info("begin to apply log on state machine, local server=" + raftNode.getRaftServer().getServerId());
        //can't longer than leader
        long newCommitIndex = Math.min(request.getLeaderCommit(),
                request.getPrevLogIndex() + request.getLogEntries().size());
        raftNode.getRaftLog().setCommitIndex(newCommitIndex);
        //apply on state machine
        if (raftNode.getRaftLog().getLastApplied() < raftNode.getRaftLog().getCommitIndex()) {
            for (long index = raftNode.getRaftLog().getLastApplied() + 1;
                    index <= raftNode.getRaftLog().getCommitIndex(); index++) {
                RaftLog.LogEntry logEntry = raftNode.getRaftLog().getEntry(index);
                if (logEntry != null) {
                    raftNode.getStateMachine().submit(logEntry.getData());
                }
                raftNode.getRaftLog().setLastApplied(index);
            }
        }
    }
}
