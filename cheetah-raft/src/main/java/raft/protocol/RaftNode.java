package raft.protocol;

import org.apache.log4j.Logger;
import raft.core.RaftCore;
import raft.core.RaftListener;
import raft.core.server.RaftServer;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc
 */
public class RaftNode implements RaftListener{

    private static final Logger log = Logger.getLogger(RaftNode.class);

    //服务器最后一次知道的任期号
    private int currentTerm = 0;
    //在当前获得选票的候选人的 Id (default 0)
    private int votedFor = 0;
    //日志条目
    private RaftLog raftLog;

    //对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）leader
    private long nextIndex;
    //对于每一个服务器，已经复制给他的日志的最高索引值
    private long matchIndex;

    //用于附加乳RPC时的重定向
    private int leaderId;

    private RaftServer raftServer;

    private Executor heartBeatThread;

    public RaftNode (RaftLog raftLog, RaftServer raftServer) {
        this.raftLog = raftLog;
        this.raftServer = raftServer;
    }
    private Lock lock = new ReentrantLock();

    public void onListen() {

    }

    public Lock getLock() {
        return lock;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }

    public int getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(int votedFor) {
        this.votedFor = votedFor;
    }

    public RaftLog getRaftLog() {
        return raftLog;
    }

    public void setRaftLog(RaftLog raftLog) {
        this.raftLog = raftLog;
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

    public RaftServer getRaftServer() {
        return raftServer;
    }

    public void setRaftServer(RaftServer raftServer) {
        this.raftServer = raftServer;
    }

    public Executor getHeartBeatThread() {
        return heartBeatThread;
    }

    public void setHeartBeatThread(Executor heartBeatThread) {
        this.heartBeatThread = heartBeatThread;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }
}
