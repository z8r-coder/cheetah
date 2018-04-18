package raft.core;

import mock.RaftMock;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.core.server.ServerNode;
import raft.protocol.*;
import rpc.async.RpcCallback;
import utils.Configuration;
import utils.ParseUtils;

import java.util.*;
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
    int asyncVoteNum;
    private RaftOptions raftOptions;
    private Map<Integer, String> serverList;
    private Map<Integer, ServerNode> serverNodeCache = new ConcurrentHashMap<>();

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionScheduledFuture;
    private ScheduledFuture heartBeatScheduledFuture;

    public RaftCore (RaftOptions raftOptions,RaftNode raftNode,
                     Map<Integer, String> serverList) {
        this.raftOptions = raftOptions;
        this.raftNode = raftNode;
        this.serverList = serverList;
        this.asyncVoteNum = 0;
        init();
    }
    public void init() {
        for (Map.Entry<Integer, String> entry : serverList.entrySet()) {
            if (!serverNodeCache.containsKey(entry.getKey()) &&
                     !entry.getKey().equals(raftNode.getRaftServer().getServerId())) {
                RaftVoteAsyncCallBack  asyncCallBack = new RaftVoteAsyncCallBack();
                String serverInfo = entry.getValue();
                CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverInfo);
                RaftServer raftServer = new RaftServer(cheetahAddress.getHost(), cheetahAddress.getPort());
                ServerNode serverNode = new ServerNode(raftServer, asyncCallBack);
                serverNodeCache.put(entry.getKey(), serverNode);
            }
        }

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

        logger.info("become leader: serverId:" + raftServer.getServerId() + "in term:" + raftNode.getCurrentTerm());

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
        for (Integer serverId : serverList.keySet()) {
            if (serverId == raftNode.getRaftServer().getServerId()) {
                continue;
            }
            // TODO: 2018/4/10 当有新机器加入时 可能为空
            final ServerNode serverNode = serverNodeCache.get(serverId);
            executorService.submit(new Runnable() {
                public void run() {
                    appendEntries(serverNode);
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

    // TODO: 2018/4/17 raft mock
    public void resetElectionTimer() {
        if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
            electionScheduledFuture.cancel(true);
        }
        //timeout
        electionScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                startNewElection();
            }
        }, RaftMock.getElectionTimeOutMs(), RaftMock.getElectionTimeOutMs(), RaftMock.getRaftMockTimeUnit());
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

            final ServerNode serverNode = serverNodeCache.get(serverId);
            executorService.submit(new Runnable() {
                public void run() {
                    //async req
                    requestVoteFor(serverNode);
                }
            });
        }
        resetElectionTimer();
    }

    /**
     * rpc call
     * @param serverNode
     */
    public void appendEntries (ServerNode serverNode) {
        RaftServer remoteRaftServer = serverNode.getRaftServer();
        RaftServer localRaftServer = raftNode.getRaftServer();
        AddRequest request = new AddRequest(raftNode.getCurrentTerm(),
                raftNode.getLeaderId(), raftNode.getRaftLog().getLastLogIndex(),
                raftNode.getRaftLog().getLogEntryTerm(raftNode.getRaftLog().getLastLogIndex()),
                raftNode.getRaftLog().getCommitIndex());
        request.setAddress(localRaftServer.getHost(), localRaftServer.getPort(),
                remoteRaftServer.getHost(), remoteRaftServer.getPort(),
                raftNode.getRaftServer().getServerId());

        //sync rpc call
        AddResponse response = serverNode.getRaftConsensusService().appendEntries(request);
        lock.lock();
        try {
            if (response == null) {
                logger.warn("append entries rpc fail, host=" + request.getRemoteHost() +
                " port=" + request.getRemotePort());
                if (serverList.get(request.getServerId()) == null) {
                    //down
                    serverNodeCache.remove(request.getServerId());
                    serverNode.getAsyncProxy().stopService();
                    serverNode.getSyncProxy().stopService();
                }
                return;
            }

            logger.info("Append Entries response:" + response.isSuccess() +
            " from server:" + request.getServerId() + " in term:" + response.getTerm() +
            " (my term in " + raftNode.getCurrentTerm() + ")");

            if (response.getTerm() > raftNode.getCurrentTerm()) {
                updateMore(response.getTerm());
            } else {
                if (response.isSuccess()) {
                    //success
                    serverNode.setMatchIndex(request.getPrevLogIndex() + request.getLogEntries().size());
                    serverNode.setNextIndex(serverNode.getMatchIndex() + 1);
                    if (serverList.get(request.getServerId()) != null) {
                        applyLogOnStateMachine();
                    } else {
                        //add new node
                    }
                } else {
                    serverNode.setNextIndex(response.getLastLogIndex() + 1);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * rpc call, async
     * @param serverNode
     */
    private void requestVoteFor(ServerNode serverNode) {
        try {
            logger.info("begin request vote for!");
            RaftServer remoteRaftServer = serverNode.getRaftServer();
            RaftServer localRaftServer = raftNode.getRaftServer();
            VotedRequest request = new VotedRequest(raftNode.getCurrentTerm(),
                    raftNode.getRaftServer().getServerId(),
                    raftNode.getRaftLog().getLastLogIndex(),
                    raftNode.getRaftLog().getLastLogTerm());
            request.setAddress(localRaftServer.getHost(), localRaftServer.getPort(),
                    remoteRaftServer.getHost(), remoteRaftServer.getPort(),
                    localRaftServer.getServerId());
            RaftVoteAsyncCallBack voteAsyncCallBack = (RaftVoteAsyncCallBack) serverNode.getRpcCallback();
            voteAsyncCallBack.setRequest(request);

            //async rpc call
            if (serverNode.getAsyncProxy().getRemoteProxyStatus() ==
                    serverNode.getAsyncProxy().STOP) {
                serverNode.getAsyncProxy().startService();
            }
            serverNode.getRaftAsyncConsensusService().leaderElection(request);
        } catch (Exception ex) {
            logger.error("request vote for occurs ex:", ex);
        }
    }

    /**
     * for leader applu log on state machine
     */
    public void applyLogOnStateMachine () {
        int serverNodeNum = serverList.size();
        long[] matchIndexes = new long[serverNodeNum];
        int i = 0;
        for (Integer serverId : serverList.keySet()) {
            if (serverId != raftNode.getRaftServer().getServerId()) {
                ServerNode serverNode = serverNodeCache.get(serverId);
                matchIndexes[i++] = serverNode.getMatchIndex();
            }
        }
        matchIndexes[i] = raftNode.getRaftLog().getLastLogIndex();
        Arrays.sort(matchIndexes);
        long newCommitIndex = matchIndexes[serverNodeNum / 2];
        logger.info("newCommitIndex:" + newCommitIndex + " oldCommitIndex:" + raftNode.getRaftLog().getCommitIndex());
        if (raftNode.getRaftLog().getLogEntryTerm(newCommitIndex) != raftNode.getCurrentTerm()) {
            logger.debug("newCommitTerm=" + raftNode.getRaftLog().getLogEntryTerm(newCommitIndex) +
            ", currentTerm=" + raftNode.getCurrentTerm());
        }
        if (newCommitIndex < raftNode.getRaftLog().getCommitIndex()) {
            return;
        }
        long oldCommitIndex = raftNode.getRaftLog().getCommitIndex();
        raftNode.getRaftLog().setCommitIndex(newCommitIndex);
        //sync -> state machine
        for (long index = oldCommitIndex + 1;index <= newCommitIndex;index++) {
            RaftLog.LogEntry logEntry = raftNode.getRaftLog().getEntry(index);
            raftNode.getStateMachine().submit(logEntry.getData());
        }
        raftNode.getRaftLog().setLastApplied(newCommitIndex);
        logger.debug("commitIndex=" + raftNode.getRaftLog().getCommitIndex() +
        "lastApplied=" + raftNode.getRaftLog().getLastApplied());
    }

    /**
     * async rpc call, raft async call back impl
     */
    public class RaftVoteAsyncCallBack implements RpcCallback<VotedResponse> {

        private VotedRequest request;

        public void success(VotedResponse resp) {
            lock.lock();
            logger.info("from serverId=" + resp.getServerId());
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
                        logger.info("Got vote from server=" + raftServer.getServerId() +
                                " for term=" + raftNode.getCurrentTerm());
                        logger.info("voteGrantedNum= + voteGrantedNum");
                        if (asyncVoteNum >= serverList.size() / 2) {
                            logger.info("Got majority vote, serverId=" + raftNode.getRaftServer().getServerId() +
                                    " become leader");
                            becomeLeader();
                        }
                    } else {
                        logger.info("Vote denied by server=" + raftServer.getServerId() +
                                " with term=" + resp.getTerm() +
                                ", this server's term is=" + raftNode.getCurrentTerm());
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
