package raft.core;

import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.protocol.RaftNode;
import raft.protocol.VotedRequest;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ruanxin
 * @create 2018-02-23
 * @desc
 */
public class RaftCore {
    private Logger logger = Logger.getLogger(RaftCore.class);

    private Lock lock = new ReentrantLock();

    private VotedRequest votedRequest;
    RaftNode raftNode;
    private RaftOptions raftOptions;
    private Map<Integer, String> serverList;

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionScheduledFuture;
    private ScheduledFuture heartBeatScheduledFuture;

    public RaftCore (RaftOptions raftOptions,RaftNode raftNode,
                     Map<Integer, String> serverList, VotedRequest votedRequest) {
        this.raftOptions = raftOptions;
        this.raftNode = raftNode;
        this.serverList = serverList;
        this.votedRequest = votedRequest;
        init();
    }
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        resetElectionTimer();
    }

    public void updateMore(int newTerm) {
        if (raftNode.getCurrentTerm() < newTerm) {
            raftNode.setCurrentTerm(newTerm);
            raftNode.setVotedFor(0);
        }
        RaftServer raftServer = raftNode.getRaftServer();
        raftServer.setServerState(RaftServer.NodeState.FOLLOWER);

        //stop heartBeat
        if (heartBeatScheduledFuture != null && !heartBeatScheduledFuture.isDone()) {
            heartBeatScheduledFuture.cancel(true);
        }
        resetElectionTimer();
    }

    public void resetElectionTimer() {
        if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
            electionScheduledFuture.cancel(true);
        }

        //timeout
        electionScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                startNewElection();
            }
        }, getElectionTimeOutMs(), getElectionTimeOutMs(), TimeUnit.MILLISECONDS);
    }

    private int getElectionTimeOutMs() {
        Random random = new Random();
        int randomTimeOutMs = raftOptions.getElectionTimeOutMilliSec()
                + random.nextInt(raftOptions.getElectionTimeOutRandomMilliSec());
        logger.debug("new election time is after " + randomTimeOutMs + " ms");
        return randomTimeOutMs;
    }

    private void startNewElection() {
        lock.lock();
        RaftServer raftServer = raftNode.getRaftServer();
        try {
            int serverId = raftServer.getServerId();
            if (serverList.get(serverId) == null) {
                resetElectionTimer();
                return;
            }
            int currentTerm = raftNode.getCurrentTerm() + 1;
            raftNode.setCurrentTerm(currentTerm);
            logger.info("Running for election in term " + currentTerm);
            raftNode.getRaftServer().setServerState(RaftServer.NodeState.CANDIDATE);
            raftNode.setVotedFor(serverId);
        } finally {
            lock.unlock();
        }

        for (Integer serverId : serverList.keySet()) {
            if (serverId == raftServer.getServerId()) {
                continue;
            }
            executorService.submit(new Runnable() {
                public void run() {
                    requestVoteFor(votedRequest);
                }
            });
        }
        resetElectionTimer();
    }

    private void requestVoteFor(VotedRequest request) {

    }

    public static void main(String[] args) {

    }
}
