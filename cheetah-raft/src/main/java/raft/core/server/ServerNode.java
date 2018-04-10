package raft.core.server;

import raft.core.RaftAsyncConsensusService;
import raft.core.RaftConsensusService;
import raft.protocol.VotedResponse;
import rpc.async.RpcCallback;
import rpc.client.AsyncClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.client.SyncClientRemoteExecutor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-04-10
 * @desc raft in server info in leader
 */
public class ServerNode {
    private RaftServer raftServer;
    private SimpleClientRemoteProxy syncProxy;
    private SimpleClientRemoteProxy asyncProxy;
    private RaftConsensusService raftConsensusService;
    private RaftAsyncConsensusService raftAsyncConsensusService;
    // 需要发送给follower的下一个日志条目的索引值，只对leader有效
    private long nextIndex;
    // 已复制日志的最高索引值
    private long matchIndex;
    private volatile boolean voteGranted;

    public ServerNode (RaftServer raftServer, RpcCallback<VotedResponse> rpcCallback) {
        this.raftServer = raftServer;
        //def client connect
        AbstractRpcConnector connector = new RpcNioConnector(null);
        RpcUtils.setAddress(raftServer.getHost(), raftServer.getPort(), connector);
        //def sync proxy
        SyncClientRemoteExecutor syncClientRemoteExecutor = new SyncClientRemoteExecutor(connector);
        syncProxy = new SimpleClientRemoteProxy(syncClientRemoteExecutor);

        AsyncClientRemoteExecutor asyncClientRemoteExecutor = new AsyncClientRemoteExecutor(connector, rpcCallback);
        asyncProxy = new SimpleClientRemoteProxy(asyncClientRemoteExecutor);

       raftConsensusService = syncProxy.registerRemote(RaftConsensusService.class);
       raftAsyncConsensusService = asyncProxy.registerRemote(RaftAsyncConsensusService.class);
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
}
