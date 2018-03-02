package raft.core;

import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.protocol.RaftNode;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

    RaftNode raftNode;
    private RaftOptions raftOptions;
    private Map<Integer, String> serverList;

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionScheduledFuture;
    private ScheduledFuture heartBeatScheduledFuture;

    public RaftCore (RaftOptions raftOptions,RaftNode raftNode,
                     Map<Integer, String> serverList) {
        this.raftOptions = raftOptions;
        this.raftNode = raftNode;
        this.serverList = serverList;
        init();
    }
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        resetElectionTimer();
    }

    public void resetElectionTimer() {
        if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
            electionScheduledFuture.cancel(true);
        }
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
        try {
            int serverId = raftNode.getRaftServer().getServerId();
            if (serverList.get(serverId) == null) {
                resetElectionTimer();
                return;
            }
            int currentTerm = raftNode.getCurrentTerm() + 1;
            raftNode.setCurrentTerm(currentTerm);
            logger.info("Running for election in term " + currentTerm);

        } finally {
            lock.unlock();
        }
    }
}
