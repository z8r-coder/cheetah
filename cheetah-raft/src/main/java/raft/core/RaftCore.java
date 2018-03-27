package raft.core;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.model.BaseRequest;
import raft.protocol.AddRequest;
import raft.protocol.RaftNode;
import raft.protocol.RaftResponse;
import raft.protocol.VotedRequest;
import rpc.client.SimpleClientRemoteProxy;
import rpc.client.SyncClientRemoteExecutor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import utils.ParseUtils;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
        executorService = new ThreadPoolExecutor(raftOptions.getRaftConsensusThreadNum(),
                raftOptions.getRaftConsensusThreadNum(),
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        resetElectionTimer();
    }

    public void becomeLeader() {
        RaftServer raftServer = raftNode.getRaftServer();
        raftServer.setServerState(RaftServer.NodeState.LEADER);
        raftNode.setLeaderId(raftNode.getLeaderId());

        logger.info("serverId:" + raftServer.getServerId() + "in term:" + raftNode.getCurrentTerm());

        //stop election
        if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
            electionScheduledFuture.cancel(true);
        }
        //start new heart beat
        startNewHeartBeat();
    }

    /**
     * begin heart beat
     */
    private void startNewHeartBeat() {
        logger.info("begin start heart beat. leaderId:" + raftNode.getLeaderId());
        for (String address : serverList.values()) {
            final RaftServer localServer = raftNode.getRaftServer();
            final CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
            if (StringUtils.equals(cheetahAddress.getHost(), localServer.getHost()) &&
                    cheetahAddress.getPort() == localServer.getPort()) {
                continue;
            }
            executorService.submit(new Runnable() {
                public void run() {
                    AddRequest request = new AddRequest(raftNode.getCurrentTerm(),
                            raftNode.getLeaderId(), raftNode.getRaftLog());
                    request.setAddress(localServer.getHost(), localServer.getPort(),
                            cheetahAddress.getHost(), cheetahAddress.getPort());
                    appendEntries(request);
                }
            });
        }
        resetHeartBeatTimer();
    }

    private void resetHeartBeatTimer() {
        if (heartBeatScheduledFuture != null && !heartBeatScheduledFuture.isDone()) {
            heartBeatScheduledFuture.cancel(true);
        }
        heartBeatScheduledFuture = scheduledExecutorService.schedule(new Runnable() {
            public void run() {
                startNewHeartBeat();
            }
        },raftOptions.getHeartbeatPeriodMilliseconds(), TimeUnit.MILLISECONDS);
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

        List<Future> futureList = new ArrayList<Future>();
        final List<VotedRequest> requestList = new ArrayList<VotedRequest>();

        for (Integer serverId : serverList.keySet()) {
            if (serverId == raftServer.getServerId()) {
                continue;
            }
            final CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverList.get(serverId));
            Future<RaftResponse> future = executorService.submit(new Callable<RaftResponse>() {
                public RaftResponse call() {
                    VotedRequest votedRequest = new VotedRequest(
                            raftNode.getCurrentTerm(),
                            raftNode.getRaftServer().getServerId(),
                            raftNode.getRaftLog().getLastLogIndex(),
                            raftNode.getRaftLog().getLastLogTerm());
                    votedRequest.setAddress(raftServer.getHost(), raftServer.getPort(),
                            cheetahAddress.getHost(), cheetahAddress.getPort());
                    requestList.add(votedRequest);
                    return requestVoteFor(votedRequest);
                }
            });
            //get result
            futureList.add(future);
        }
        //handle future
        electionFutureHandler(futureList, requestList);

        resetElectionTimer();
    }

    /**
     * handle the election future obj, syc
     * @param futures
     */
    private void electionFutureHandler(List<Future> futures, List<VotedRequest> requests) {
        int voteGrantedNum = 0;
        for (int i = 0; i < futures.size();i++) {
            Future<RaftResponse> future = futures.get(i);
            VotedRequest request = requests.get(i);
            try {
                RaftServer raftServer = raftNode.getRaftServer();
                RaftResponse response = future.get(raftOptions.getRaftFutureTimeOut(), TimeUnit.SECONDS);
                if (raftNode.getCurrentTerm() != request.getTerm() ||
                        raftServer.getServerState() != RaftServer.NodeState.CANDIDATE ||
                        response == null) {
                    logger.info("ignore,the state or term is wrong");
                    continue;
                }
                if (response.getTerm() > raftNode.getCurrentTerm()) {
                    logger.info("Receive resp from server:" + response.getServerId() +
                    "in term:" + response.getTerm() + "but this server was in term:" + raftNode.getCurrentTerm());
                    updateMore(response.getTerm());
                } else {
                    if (response.isGranted()) {
                        //success
                        logger.info("Got vote from server:" + raftServer.getServerId() +
                                        " for term {}" + raftNode.getCurrentTerm());
                        voteGrantedNum += 1;
                        logger.info("voteGrantedNum= + voteGrantedNum");
                    } else {
                        logger.info("Vote denied by server {}" + raftServer.getServerId() +
                                        " with term {}" + response.getTerm() +
                                        ", this server's term is {}" + raftNode.getCurrentTerm());
                    }
                }

            } catch (Exception e) {
                logger.error("electionFutureHandler occurs exception:", e);
                continue;
            }
        }
        if (voteGrantedNum > serverList.size() / 2) {
            //success to become leader
            becomeLeader();
        }
    }

    /**
     * rpc call
     * @param request
     */
    private RaftResponse requestVoteFor(BaseRequest request) {
        //def client connect
        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(request.getRemoteHost(), request.getRemotePort(), connector);
        SyncClientRemoteExecutor clientRemoteExecutor = new SyncClientRemoteExecutor(connector);
        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
        proxy.startService();

        RaftConsensusService raftConsensusService = proxy.registerRemote(RaftConsensusService.class);
        RaftResponse response;

        if (request instanceof VotedRequest) {
            //vote
            response = raftConsensusService.leaderElection((VotedRequest) request);
        } else {
            //append entries
            response = raftConsensusService.appendEntries((AddRequest) request);
        }
        return response;
    }

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(20,
                20,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        List<Future> list = new ArrayList<Future>();
        for (int i = 0; i < 20 ; i++) {
            Future<String> future = executorService.submit(new Callable<String>() {
                public String call() {
                    return "1";
                }
            });
            list.add(future);
        }
        for (Future future : list) {
            try {
                System.out.println(future.get(5,TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
