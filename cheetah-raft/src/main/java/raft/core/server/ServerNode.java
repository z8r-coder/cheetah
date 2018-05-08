package raft.core.server;

import constants.Globle;
import mock.RaftMock;
import org.apache.log4j.Logger;
import raft.core.RaftAsyncConsensusService;
import raft.core.RaftConsensusService;
import raft.core.rpc.RaftRpcServerAcceptor;
import raft.demo.callback.TestCallBack;
import raft.protocol.RaftLog;
import raft.protocol.response.VotedResponse;
import rpc.async.RpcCallback;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.client.SyncClientRemoteExecutor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;

import java.util.List;

/**
 * @author ruanxin
 * @create 2018-04-10
 * @desc raft in server info in leader
 */
public class ServerNode {

    private Logger logger = Logger.getLogger(ServerNode.class);

    private RaftServer raftServer;
    private SimpleClientRemoteProxy syncProxy;
    private SimpleClientRemoteProxy asyncProxy;
    private RaftConsensusService raftConsensusService;
    private RaftAsyncConsensusService raftAsyncConsensusService;
    private RpcCallback<VotedResponse> rpcCallback;

    private long prevLogIndex;
    private long commitIndex;
    private List<RaftLog.LogEntry> entries;

    // 需要发送给follower的下一个日志条目的索引值，只对leader有效
    private long nextIndex;
    // 已复制日志的最高索引值
    private long matchIndex;
    private volatile boolean voteGranted;

    public ServerNode (RaftServer raftServer, RpcCallback<VotedResponse> rpcCallback) {
        try {
            this.raftServer = raftServer;
            this.rpcCallback = rpcCallback;
            //def sync client connect
            AbstractRpcConnector syncConnector = new RpcNioConnector(null);
            RpcUtils.setAddress(raftServer.getHost(), raftServer.getPort(), syncConnector);
            //def async client connect
            AbstractRpcConnector asyncConnector = new RpcNioConnector(null);
            RpcUtils.setAddress(raftServer.getHost(), raftServer.getPort(), asyncConnector);
            //def sync proxy
            SyncClientRemoteExecutor syncClientRemoteExecutor = new SyncClientRemoteExecutor(syncConnector);
            syncProxy = new SimpleClientRemoteProxy(syncClientRemoteExecutor);

            AsyncClientRemoteExecutor asyncClientRemoteExecutor = new AsyncClientRemoteExecutor(asyncConnector, rpcCallback);
            asyncProxy = new SimpleClientRemoteProxy(asyncClientRemoteExecutor);

            raftConsensusService = syncProxy.registerRemote(RaftConsensusService.class);
            raftAsyncConsensusService = asyncProxy.registerRemote(RaftAsyncConsensusService.class);
        } catch (Exception ex) {
            logger.error("occurs ex:", ex);
        }
    }

    public void startService() {
        if (syncProxy != null && syncProxy.getRemoteProxyStatus() ==
                SimpleClientRemoteProxy.STOP) {
            syncProxy.startService();
        }
        if (asyncProxy != null && asyncProxy.getRemoteProxyStatus() ==
                SimpleClientRemoteProxy.STOP) {
            asyncProxy.startService();
        }
    }
    public void stopSerivce() {
        if (syncProxy != null && syncProxy.getRemoteProxyStatus() ==
                SimpleClientRemoteProxy.STARTED) {
            syncProxy.stopService();
        }
        if (asyncProxy != null && asyncProxy.getRemoteProxyStatus() ==
                SimpleClientRemoteProxy.STARTED) {
            asyncProxy.stopService();
        }
    }

    public static void main(String[] args) {
        RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(RaftMock.rootPathMapping.get(Globle.localPortTest1),
                Globle.localHost, Globle.localPortTest1);
        acceptor.startService();

        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(Globle.localHost, Globle.localPortTest1, connector);
        // sync
        SyncClientRemoteExecutor syncClientRemoteExecutor = new SyncClientRemoteExecutor(connector);
        //async
        TestCallBack testCallBack = new TestCallBack();
//        AsyncClientRemoteExecutor asyncClientRemoteExecutor = new AsyncClientRemoteExecutor(connector, testCallBack);
        //proxy
        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(syncClientRemoteExecutor);
        proxy.startService();

//        RaftAsyncConsensusService raftAsyncConsensusService = proxy.registerRemote(RaftAsyncConsensusService.class);
        RaftConsensusService raftConsensusService = proxy.registerRemote(RaftConsensusService.class);
        raftConsensusService.appendEntries(null);
//        raftAsyncConsensusService.leaderElection(null);
//        HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
//        System.out.println(helloRpcService.getHello());

    }

    public RaftServer getRaftServer() {
        return raftServer;
    }

    public void setRaftServer(RaftServer raftServer) {
        this.raftServer = raftServer;
    }

    public SimpleClientRemoteProxy getSyncProxy() {
        return syncProxy;
    }

    public void setSyncProxy(SimpleClientRemoteProxy syncProxy) {
        this.syncProxy = syncProxy;
    }

    public SimpleClientRemoteProxy getAsyncProxy() {
        return asyncProxy;
    }

    public void setAsyncProxy(SimpleClientRemoteProxy asyncProxy) {
        this.asyncProxy = asyncProxy;
    }

    public RaftConsensusService getRaftConsensusService() {
        return raftConsensusService;
    }

    public void setRaftConsensusService(RaftConsensusService raftConsensusService) {
        this.raftConsensusService = raftConsensusService;
    }

    public RaftAsyncConsensusService getRaftAsyncConsensusService() {
        return raftAsyncConsensusService;
    }

    public void setRaftAsyncConsensusService(RaftAsyncConsensusService raftAsyncConsensusService) {
        this.raftAsyncConsensusService = raftAsyncConsensusService;
    }

    public long getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(long nextIndex) {
        this.nextIndex = nextIndex;
    }

    public long getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(long matchIndex) {
        this.matchIndex = matchIndex;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }

    public RpcCallback<VotedResponse> getRpcCallback() {
        return rpcCallback;
    }

    public void setRpcCallback(RpcCallback<VotedResponse> rpcCallback) {
        this.rpcCallback = rpcCallback;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public List<RaftLog.LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<RaftLog.LogEntry> entries) {
        this.entries = entries;
    }
}
