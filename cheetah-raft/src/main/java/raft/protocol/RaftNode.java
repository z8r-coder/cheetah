package raft.protocol;

import org.apache.log4j.Logger;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc
 */
public class RaftNode {

    private static final Logger log = Logger.getLogger(RaftNode.class);

    //服务器最后一次知道的任期号
    private int currentTerm = 0;
    //在当前获得选票的候选人的 Id
    private int votedFor;
    //日志条目
    private RaftLog raftLog;

    //对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）leader
    private long nextIndex;
    //对于每一个服务器，已经复制给他的日志的最高索引值
    private long matchIndex;

}
