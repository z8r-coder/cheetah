package raft.core;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.protocol.AddRequest;
import raft.protocol.RaftNode;
import raft.protocol.VotedRequest;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import utils.ParseUtils;
import utils.StringUtils;

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

    RaftNode raftNode;
    private RaftOptions raftOptions;
    private Map<Integer, String> serverList;

    private ExecutorService executorService;
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

    public void becomeLeader() {
        raftNode.getRaftServer().setServerState(RaftServer.NodeState.LEADER);
        raftNode.setLeaderId(raftNode.getLeaderId());

        //stop election
        if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
            electionScheduledFuture.cancel(true);
        }


    }

    //begin heart beat
    private void startNewHeartBeat() {
        logger.info("begin start heart beat. leaderId:" + raftNode.getLeaderId());
        for (String address : serverList.values()) {
            final CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
            if (StringUtils.equals(cheetahAddress.getHost(), raftNode.getRaftServer().getHost()) &&
                    cheetahAddress.getPort() == raftNode.getRaftServer().getPort()) {
                continue;
            }
            executorService.submit(new Runnable() {
                public void run() {
                    AddRequest request = new AddRequest(raftNode.getCurrentTerm(),
                            raftNode.getLeaderId(), raftNode.getRaftLog());
                    appendEntries(request);
                }
            });
        }
    }

    //附加RPC
    public void appendEntries (AddRequest request) {

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
        final RaftServer raftServer = raftNode.getRaftServer();
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
            final CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverList.get(serverId));
            executorService.submit(new Runnable() {
                public void run() {
                    VotedRequest votedRequest = new VotedRequest(
                            raftNode.getCurrentTerm(),
                            raftNode.getRaftServer().getServerId(),
                            raftNode.getRaftLog().getLastLogIndex(),
                            raftNode.getRaftLog().getLastLogTerm());
                    votedRequest.setAddress(raftServer.getHost(), raftServer.getPort(),
                            cheetahAddress.getHost(), cheetahAddress.getPort());
                    requestVoteFor(votedRequest);
                }
            });
        }
        resetElectionTimer();
    }

    /**
     * rpc call
     * @param request
     */
    private void requestVoteFor(VotedRequest request) {
        //def client connect
        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(request.getRemoteHost(), request.getRemotePort(), connector);
        SimpleClientRemoteExecutor clientRemoteExecutor = new SimpleClientRemoteExecutor(connector);
        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
        proxy.startService();

        RaftConsensusService raftConsensusService = proxy.registerRemote(RaftConsensusService.class);
        raftConsensusService.leaderElection(request);
    }

    public static void main(String[] args) {

    }
}
