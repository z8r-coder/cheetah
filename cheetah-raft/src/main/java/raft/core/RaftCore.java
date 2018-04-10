package raft.core;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.protocol.*;
import rpc.async.RpcCallback;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.client.SyncClientRemoteExecutor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;
import utils.Configuration;
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
    private Configuration configuration;

    RaftNode raftNode;
    int asyncVoteNum;
    private RaftOptions raftOptions;
    private Map<Integer, String> serverList;
    private Map<Integer, SimpleClientRemoteProxy> rpcAsyncClientCache = new ConcurrentHashMap<Integer, SimpleClientRemoteProxy>();
    private Map<Integer, SimpleClientRemoteProxy> rpcSyncClientCache = new ConcurrentHashMap<Integer, SimpleClientRemoteProxy>();

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionScheduledFuture;
    private ScheduledFuture heartBeatScheduledFuture;

    public RaftCore (RaftOptions raftOptions,RaftNode raftNode,
                     Map<Integer, String> serverList) {
        this.raftOptions = raftOptions;
        this.raftNode = raftNode;
        this.serverList = serverList;
        this.configuration = new Configuration();
        this.asyncVoteNum = 0;
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
        raftNode.setLeaderId(raftServer.getServerId());

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
                            raftNode.getLeaderId(), raftNode.getRaftLog().getLastLogIndex(),
                            raftNode.getRaftLog().getLogEntryTerm(raftNode.getRaftLog().getLastLogIndex()),
                            raftNode.getRaftLog().getCommitIndex());
                    request.setAddress(localServer.getHost(), localServer.getPort(),
                            cheetahAddress.getHost(), cheetahAddress.getPort(),
                            raftNode.getRaftServer().getServerId());
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

        //async vote call back handler
        final RaftVoteAsyncCallBack raftVoteAsyncCallBack = new RaftVoteAsyncCallBack();
        for (Integer serverId : serverList.keySet()) {
            if (serverId == raftServer.getServerId()) {
                continue;
            }
            final CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverList.get(serverId));
            //build request
            final VotedRequest votedRequest = new VotedRequest(
                    raftNode.getCurrentTerm(),
                    raftNode.getRaftServer().getServerId(),
                    raftNode.getRaftLog().getLastLogIndex(),
                    raftNode.getRaftLog().getLastLogTerm());
            votedRequest.setAddress(raftServer.getHost(), raftServer.getPort(),
                    cheetahAddress.getHost(), cheetahAddress.getPort(),
                    raftServer.getServerId());
            raftVoteAsyncCallBack.setRequest(votedRequest);
            executorService.submit(new Runnable() {
                public void run() {
                    //async req
                    requestVoteFor(votedRequest, raftVoteAsyncCallBack);
                }
            });
        }
        resetElectionTimer();
    }

    /**
     * rpc call
     * @param request
     */
    public void appendEntries (AddRequest request) {
        SimpleClientRemoteProxy proxy = null;
        if (rpcSyncClientCache.get(request.getServerId()) == null) {
            //def client connect
            AbstractRpcConnector connector = new RpcNioConnector(null);
            RpcUtils.setAddress(request.getRemoteHost(), request.getRemotePort(), connector);
            SyncClientRemoteExecutor clientRemoteExecutor = new SyncClientRemoteExecutor(connector);

            proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
            proxy.startService();

            rpcSyncClientCache.put(request.getServerId(), proxy);
        } else {
            proxy = rpcSyncClientCache.get(request.getServerId());
            if (proxy.getRemoteProxyStatus() == SimpleClientRemoteProxy.STOP) {
                proxy.startService();
            }
            //have started
        }
        RaftConsensusService raftConsensusService = proxy.registerRemote(RaftConsensusService.class);
        //sync rpc call
        AddResponse response = raftConsensusService.appendEntries(request);
        lock.lock();
        try {
            if (response == null) {
                logger.warn("append entries rpc fail, host=" + request.getRemoteHost() +
                " port=" + request.getRemotePort());
                
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * rpc call, async
     * @param request
     */
    private void requestVoteFor(VotedRequest request, RpcCallback raftVoteAsyncCallBack) {
        SimpleClientRemoteProxy proxy = null;
        if (rpcAsyncClientCache.get(request.getServerId()) == null) {
            //def client connect
            AbstractRpcConnector connector = new RpcNioConnector(null);
            RpcUtils.setAddress(request.getRemoteHost(), request.getRemotePort(), connector);

            //def vote callback
            AsyncClientRemoteExecutor clientRemoteExecutor = new AsyncClientRemoteExecutor(connector, raftVoteAsyncCallBack);

            proxy = new SimpleClientRemoteProxy(clientRemoteExecutor);
            proxy.startService();

            rpcAsyncClientCache.put(request.getServerId(), proxy);
        } else {
            proxy = rpcAsyncClientCache.get(request.getServerId());
            if (proxy.getRemoteProxyStatus() == SimpleClientRemoteProxy.STOP) {
                proxy.startService();
            }
            //have started
        }

        RaftConsensusService raftConsensusService = proxy.registerRemote(RaftConsensusService.class);
        //async rpc call
        raftConsensusService.leaderElection(request);
    }

    /**
     * async rpc call, raft async call back impl
     */
    public class RaftVoteAsyncCallBack implements RpcCallback<VotedResponse> {

        private VotedRequest request;

        public void success(VotedResponse resp) {
            lock.lock();
            try {
                RaftServer raftServer = raftNode.getRaftServer();

                if (raftNode.getCurrentTerm() != request.getTerm() ||
                        raftServer.getServerState() != RaftServer.NodeState.CANDIDATE ||
                        resp == null) {
                    logger.info("ignore,the state or term is wrong");
                    return;
                }
                if (resp.getTerm() > raftNode.getCurrentTerm()) {
                    logger.info("Receive resp from server:" + resp.getServerId() +
                            "in term:" + resp.getTerm() + "but this server was in term:" + raftNode.getCurrentTerm());
                    updateMore(resp.getTerm());
                } else {
                    if (resp.isGranted()) {
                        //success
                        asyncVoteNum += 1;
                        logger.info("Got vote from server:" + raftServer.getServerId() +
                                " for term {}" + raftNode.getCurrentTerm());
                        logger.info("voteGrantedNum= + voteGrantedNum");
                        if (asyncVoteNum > serverList.size() / 2) {
                            logger.info("Got majority vote, serverId={}" + raftNode.getRaftServer().getServerId() +
                                    " become leader");
                            becomeLeader();
                        }
                    } else {
                        logger.info("Vote denied by server {}" + raftServer.getServerId() +
                                " with term {}" + resp.getTerm() +
                                ", this server's term is {}" + raftNode.getCurrentTerm());
                    }
                }
            }finally{
                lock.unlock();
            }
        }

        public void fail(Throwable t) {
            logger.warn("Call Back fail from server host:" + request.getRemoteHost() +
            " port:" + request.getRemotePort(), t);
        }

        public VotedRequest getRequest() {
            return request;
        }

        public void setRequest(VotedRequest request) {
            this.request = request;
        }
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
