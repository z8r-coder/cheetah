package raft.core;

import constants.ErrorCodeEnum;
import mock.RaftMock;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.constants.RaftOptions;
import raft.core.server.RaftServer;
import raft.core.server.ServerNode;
import raft.model.BaseRequest;
import raft.protocol.RaftLog;
import raft.protocol.RaftNode;
import raft.protocol.request.*;
import raft.protocol.response.*;
import raft.utils.RaftUtils;
import rpc.async.RpcCallback;
import rpc.exception.RpcException;
import utils.ParseUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
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
    private Condition commitIndexCondition = lock.newCondition();

    private RaftNode raftNode;
    private int asyncVoteNum;
    private RaftOptions raftOptions;
    private Map<Long, String> serverList;
    private Map<Long, ServerNode> serverNodeCache = new ConcurrentHashMap<>();

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture electionScheduledFuture;
    private ScheduledFuture heartBeatScheduledFuture;

    //同步进行中
    public final static int SYNC_ING = 0;
    //同步失败
    public final static int SYNC_FAIL = 1;
    //同步成功
    public final static int SYNC_SUCC = 2;

    public RaftCore (RaftOptions raftOptions,RaftNode raftNode,
                     Map<Long, String> serverList) {
        this.raftOptions = raftOptions;
        this.raftNode = raftNode;
        this.serverList = serverList;
        this.asyncVoteNum = 0;
        init();
    }
    public void init() {
        for (Map.Entry<Long, String> entry : serverList.entrySet()) {
            RaftVoteAsyncCallBack  asyncCallBack = new RaftVoteAsyncCallBack();
            String serverInfo = entry.getValue();
            CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverInfo);
            RaftServer raftServer = new RaftServer(cheetahAddress.getHost(), cheetahAddress.getPort());
            ServerNode serverNode = new ServerNode(raftServer, asyncCallBack);
            serverNodeCache.put(entry.getKey(), serverNode);
        }

        executorService = new ThreadPoolExecutor(raftOptions.getRaftConsensusThreadNum(),
                raftOptions.getRaftConsensusThreadNum(),
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        resetElectionTimer();
    }

    /**
     * new node add to cluster
     */
    public RegisterServerResponse newNodeRegister(RegisterServerRequest request) {
        long newServerId = ParseUtils.generateServerId(request.getNewHost(), request.getNewPort());
        try {
            serverList.put(newServerId, request.getNewHost() + ":" + request.getNewPort());
            RaftVoteAsyncCallBack asyncCallBack = new RaftVoteAsyncCallBack();
            RaftServer raftServer = new RaftServer(request.getNewHost(), request.getNewPort());
            ServerNode serverNode = new ServerNode(raftServer, asyncCallBack);
            serverNodeCache.put(newServerId, serverNode);
            RegisterServerResponse registerServerResponse = new RegisterServerResponse(raftNode.getRaftServer().getServerId(),
                    serverList, raftNode.getLeaderId(), raftNode.getCurrentTerm(), true);
            logger.info("new node serverId=" + newServerId + " register successful!");
            return registerServerResponse;
        } catch (Exception ex) {
            logger.error("new node serverId=" + newServerId + "register occurs ex:", ex);
            RegisterServerResponse registerServerResponse = new RegisterServerResponse(raftNode.getRaftServer().getServerId());
            registerServerResponse.setSuccessful(false);
            return registerServerResponse;
        }
    }

    /**
     * become leader
     */
    public void becomeLeader() {
        RaftServer raftServer = raftNode.getRaftServer();
        raftServer.setServerState(RaftServer.NodeState.LEADER);
        raftNode.setLeaderId(raftServer.getServerId());

        logger.info("become leader: serverId:" + raftServer.getServerId() + " in term:" + raftNode.getCurrentTerm());

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
        for (Long serverId : serverList.keySet()) {
            if (serverId == raftNode.getRaftServer().getServerId()) {
                continue;
            }
            final ServerNode serverNode;
            if (serverNodeCache.get(serverId) == null) {
                //new serverNode sync
                serverNode = initNewServerNode(serverId);
            } else {
                serverNode = serverNodeCache.get(serverId);
            }
            serverNode.setEntries(new ArrayList<RaftLog.LogEntry>());
            serverNode.setCommitIndex(raftNode.getRaftLog().getCommitIndex());
            serverNode.setPrevLogIndex(raftNode.getRaftLog().getLastLogIndex());
            executorService.submit(new Runnable() {
                public void run() {
                    appendEntries(serverNode);
                }
            });
        }
        resetHeartBeatTimer();
    }

    /**
     * init new serverNode
     */
    private ServerNode initNewServerNode (long serverId) {
        RaftVoteAsyncCallBack asyncCallBack = new RaftVoteAsyncCallBack();
        String serverInfo = serverList.get(serverId);
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(serverInfo);
        RaftServer raftServer = new RaftServer(cheetahAddress.getHost(), cheetahAddress.getPort());
        ServerNode serverNode = new ServerNode(raftServer, asyncCallBack);
        serverNodeCache.put(serverId, serverNode);
        return serverNode;
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
            long serverId = raftServer.getServerId();
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

        if (serverList.size() == 1 && serverList.get(raftServer.getServerId()) != null) {
            //only one node, become leader
            logger.warn("there is only one node, so I will become leader!");
            becomeLeader();
            return;
        }

        for (Long serverId : serverList.keySet()) {
            if (serverId == raftServer.getServerId()) {
                continue;
            }
            final ServerNode serverNode;
            if (serverNodeCache.get(serverId) == null) {
                //new server node need sync
                serverNode = initNewServerNode(serverId);
            } else {
                serverNode = serverNodeCache.get(serverId);
            }
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
     * set redirect leader
     */
    public SetKVResponse setRedirectLeader (SetKVRequest request) {
        ServerNode serverNode = getServerNodeById(raftNode.getLeaderId());
        return serverNode.getRaftConsensusService().setKV(request);
    }

    /**
     * get redirect leader
     */
    public GetValueResponse getRedirectLeader (GetValueRequest request) {
        ServerNode serverNode = getServerNodeById(raftNode.getLeaderId());
        return serverNode.getRaftConsensusService().getValue(request);
    }

    /**
     * register server redirect leader
     */
    public RegisterServerResponse registerRedirectLeader (RegisterServerRequest request) {
        ServerNode serverNode = getServerNodeById(raftNode.getLeaderId());
        return serverNode.getRaftConsensusService().registerServer(request);
    }

    /**
     * get serverNode by server node cache
     * @param serverId
     * @return
     */
    private ServerNode getServerNodeById(long serverId) {
        ServerNode serverNode = serverNodeCache.get(serverId);
        //sync rpc call
        if (serverNode.getSyncProxy().getRemoteProxyStatus() ==
                serverNode.getSyncProxy().STOP) {
            logger.debug("serverId=" + serverNode.getRaftServer().getServerId() + "" +
                    " need to start sync proxy!");
            serverNode.getSyncProxy().startService();
        }
        return serverNode;
    }

    /**
     * get value
     */
    public byte[] getValue (String key) {
        return raftNode.getStateMachine().get(key);
    }

    /**
     * log replication
     */
    public boolean logReplication (byte[] data) {
        lock.lock();
        long newLastLogIndex;
        long prevLogIndex;
        long commitIndex;
        try {
            if (raftNode.getRaftServer().getServerState() !=
                    RaftServer.NodeState.LEADER) {
                logger.debug("local serverId=" + raftNode.getRaftServer().getServerId() + " not leader!");
                return false;
            }
            prevLogIndex = raftNode.getRaftLog().getLastLogIndex();
            commitIndex = raftNode.getRaftLog().getCommitIndex();

            long logIndex;
            if (raftNode.getRaftLog().getLogMetaDataMap().size() != 0) {
                logIndex = raftNode.getRaftLog().getLastLogIndex() + 1;
            } else {
                //there is no file
                logIndex = raftNode.getRaftLog().getLastLogIndex();
            }

            logger.info("logReplication serverId=" + raftNode.getRaftServer().getServerId() +
            " ,logIndex=" + logIndex);
            RaftLog.LogEntry logEntry = new RaftLog.LogEntry(raftNode.getCurrentTerm(),
                    logIndex, data);
            final List<RaftLog.LogEntry> entries = new ArrayList<>();
            entries.add(logEntry);

            //set raft log protocol meta info
            newLastLogIndex = raftNode.getRaftLog().append(entries);

            for (Long serverId : serverList.keySet()) {
                if (serverId == raftNode.getRaftServer().getServerId()) {
                    continue;
                }

                final ServerNode serverNode;
                if (serverNodeCache.get(serverId) == null) {
                    //new server node need sync
                    serverNode = initNewServerNode(serverId);
                } else {
                    serverNode = serverNodeCache.get(serverId);
                }
                serverNode.setPrevLogIndex(prevLogIndex);
                serverNode.setCommitIndex(commitIndex);
                serverNode.setEntries(entries);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        appendEntries(serverNode);
                    }
                });
            }

            //sync commitIndex >= newLastLogIndex
            long startTime = System.currentTimeMillis();
            while (raftNode.getRaftLog().getLastApplied() < newLastLogIndex) {
                if (System.currentTimeMillis() - startTime >= raftOptions.getMaxAwaitTimeout()) {
                    break;
                }
                commitIndexCondition.await(raftOptions.getMaxAwaitTimeout(), TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            logger.info("logReplication occurs ex!",ex);
            return false;
        } finally {
            lock.unlock();
        }
        logger.info("lastAppliedIndex=" + raftNode.getRaftLog().getLastApplied() +
        " ,newLastLogIndex=" + newLastLogIndex);
        if (raftNode.getRaftLog().getLastApplied() < newLastLogIndex) {
            return false;
        }
        return true;
    }
    /**
     * rpc call
     * @param serverNode
     */
    public void appendEntries (ServerNode serverNode) {
        lock.lock();
        RaftServer localRaftServer = raftNode.getRaftServer();
        RaftServer remoteRaftServer = serverNode.getRaftServer();

        int numCount = serverNode.getEntries().size();
        AddRequest request = new AddRequest(raftNode.getCurrentTerm(),
                raftNode.getLeaderId(),serverNode.getPrevLogIndex(),
                raftNode.getRaftLog().getLogEntryTerm(serverNode.getPrevLogIndex()),
                //truncateSuffix
                Math.min(serverNode.getPrevLogIndex(),
                        serverNode.getCommitIndex()) + numCount, serverNode.getEntries(),
                serverList);

        request.setAddress(localRaftServer.getHost(), localRaftServer.getPort(),
                remoteRaftServer.getHost(), remoteRaftServer.getPort(),
                raftNode.getRaftServer().getServerId());
        try {
            //sync rpc call
            if (serverNode.getSyncProxy().getRemoteProxyStatus() ==
                    serverNode.getSyncProxy().STOP) {
                serverNode.getSyncProxy().startService();
            }
            AddResponse response = serverNode.getRaftConsensusService().appendEntries(request);

            if (response == null) {
                logger.warn("append entries rpc fail, host=" + request.getRemoteHost() +
                " port=" + request.getRemotePort() + " may down, remove it!");
                //down
                long remoteServerId = ParseUtils.generateServerId(request.getRemoteHost(),
                        request.getRemotePort());
                serverList.remove(remoteServerId);
                serverNodeCache.remove(remoteServerId);
                serverNode.stopSerivce();
                return;
            }

            logger.info("Append Entries response:" + response.isSuccess() +
            " from server:" + request.getServerId() + " in term:" + response.getTerm() +
            " (my term in " + raftNode.getCurrentTerm() + ")" + " ,entries size=" + numCount);

            if (response.getTerm() > raftNode.getCurrentTerm()) {
                updateMore(response.getTerm());
            } else {
                if (response.isSuccess()) {
                    //success
                    serverNode.setMatchIndex(response.getLastLogIndex());
                    serverNode.setNextIndex(serverNode.getMatchIndex() + 1);
                    applyLogOnStateMachine();
                } else {
                    serverNode.setNextIndex(response.getLastLogIndex() + 1);

                    if (response.getLastLogIndex() < raftNode.getRaftLog().getLastLogIndex()) {
                        logger.info("local serverId=" + raftNode.getRaftServer().getServerId() +
                                "from serverId=" + response.getServerId() +
                        " ,lastPrevIndex=" + response.getLastLogIndex() + " need sync log entry!");
                        List<RaftLog.LogEntry> entries = new ArrayList<>();
                        for (long i = response.getLastLogIndex() + 1; i <= raftNode.getRaftLog().getLastLogIndex();i++) {
                            entries.add(raftNode.getRaftLog().getEntry(i));
                        }
                        SyncLogEntryRequest syncLogEntryRequest = new SyncLogEntryRequest(raftNode.getRaftServer().getServerId(),
                                entries, request.getLeaderCommit());
                        //sync log data, sync rpc call
                        if (serverNode.getSyncProxy().getRemoteProxyStatus() ==
                                serverNode.getSyncProxy().STOP) {
                            serverNode.getSyncProxy().startService();
                        }
                        SyncLogEntryResponse syncLogEntryResponse = serverNode.getRaftConsensusService().syncLogEntry(syncLogEntryRequest);
                        if (syncLogEntryResponse.getSyncStatus() ==
                                RaftCore.SYNC_SUCC) {
                            //sync log entry success
                            logger.info("from serverId=" + syncLogEntryResponse.getServerId() +
                            " sync log entry successful!");
                            serverNode.setMatchIndex(syncLogEntryResponse.getLastLogIndex());
                            serverNode.setNextIndex(serverNode.getMatchIndex() + 1);
                            applyLogOnStateMachine();
                        } else if (syncLogEntryResponse.getSyncStatus() ==
                                RaftCore.SYNC_FAIL){
                            logger.info("from serverId=" + syncLogEntryResponse.getServerId() +
                            " sync log entry fail!");
                            serverNode.setNextIndex(syncLogEntryResponse.getLastLogIndex() + 1);
                        } else {
                            logger.info("from serverId=" + syncLogEntryResponse.getServerId() +
                            " sync log entry ing!");
                        }
                    }
                }
            }
            //sync serverNode and serverList
            RaftUtils.syncServerNodeAndServerList(serverNodeCache, serverList,
                    raftNode.getRaftServer().getServerId());
        } catch (Exception ex) {
            logger.error("appendEntries occurs ex:" + ex.getMessage(), ex);
            //server down
            serverDownAndRemove(ex, request, serverNode, "appendEntries");
        } finally {
            lock.unlock();
        }
    }

    /**
     * rpc call, async
     * @param serverNode
     */
    private void requestVoteFor(ServerNode serverNode) {
        RaftServer remoteRaftServer = serverNode.getRaftServer();
        RaftServer localRaftServer = raftNode.getRaftServer();
        logger.info("serverId=" + raftNode.getRaftServer().getServerId() +
                " begin request vote for! remote host=" + remoteRaftServer.getHost() +
        " ,remote port=" + remoteRaftServer.getPort());
        VotedRequest request = new VotedRequest(raftNode.getCurrentTerm(),
                raftNode.getRaftServer().getServerId(),
                raftNode.getRaftLog().getLastLogIndex(),
                raftNode.getRaftLog().getLastLogTerm());
        request.setAddress(localRaftServer.getHost(), localRaftServer.getPort(),
                remoteRaftServer.getHost(), remoteRaftServer.getPort(),
                localRaftServer.getServerId());
        try {
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
            //server down
            serverDownAndRemove(ex, request, serverNode, "requestVoteFor");
        }
    }

    /**
     * server down handler
     */
    private void serverDownAndRemove (Exception ex, BaseRequest request,
                                      ServerNode serverNode, String message) {
        if (ex instanceof RpcException &&
                ((RpcException) ex).getErrorCode().equals(ErrorCodeEnum.RPC00020.getErrorCode())) {
            removeAndStopServer(message, request, serverNode);
        } else if (ex instanceof RpcException &&
                ((RpcException) ex).getErrorCode().equals(ErrorCodeEnum.RPC00010.getErrorCode())) {
            //发送超时，尝试连接来判定是否down机
            serverNode.stopSerivce();
            try {
                serverNode.startService();
            } catch (Exception e) {
                if (e instanceof RpcException &&
                        ((RpcException) ex).getErrorCode().equals(ErrorCodeEnum.RPC00020.getErrorCode())) {
                    removeAndStopServer(message, request, serverNode);
                }
            }
        }
    }

    private void removeAndStopServer (String message, BaseRequest request,
                                      ServerNode serverNode) {
        logger.warn(message + " rpc fail, host=" + request.getRemoteHost() +
                " port=" + request.getRemotePort() + " may down, remove it!");
        long remoteServerId = ParseUtils.generateServerId(request.getRemoteHost(),
                request.getRemotePort());
        serverList.remove(remoteServerId);
        serverNodeCache.remove(remoteServerId);
        serverNode.stopSerivce();
    }

    /**
     * for leader apply log on state machine
     */
    public void applyLogOnStateMachine () {
        if (!raftNode.getRaftLog().existLogEntry()) {
            //don't exist log entry
            return;
        }
        int serverNodeNum = serverList.size();
        long[] matchIndexes = new long[serverNodeNum];
        int i = 0;
        for (Long serverId : serverList.keySet()) {
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
            logger.info("from serverId=" + resp.getServerId());
            lock.lock();
            try {
                RaftServer raftServer = raftNode.getRaftServer();
                if (raftServer.getServerState() == RaftServer.NodeState.LEADER) {
                    logger.info("ServerId=" + raftServer.getServerId() + "have been leader!");
                    return;
                }
                if (raftNode.getCurrentTerm() != request.getTerm() ||
                        raftServer.getServerState() != RaftServer.NodeState.CANDIDATE ||
                        resp == null) {
                    logger.info("ignore,the state or term is wrong, local term=" + raftNode.getCurrentTerm() +
                    " ,remote term=" + request.getTerm() + " ,local server state=" + raftServer.getServerState());
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
                        logger.info("voteGrantedNum=" + asyncVoteNum);
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

    public Map<Long, String> getServerList() {
        return serverList;
    }

    public void setServerList(Map<Long, String> serverList) {
        this.serverList = serverList;
    }

    public void setServerNodeCache(Map<Long, ServerNode> serverNodeCache) {
        this.serverNodeCache = serverNodeCache;
    }

    public Map<Long, ServerNode> getServerNodeCache() {
        return serverNodeCache;
    }
}
