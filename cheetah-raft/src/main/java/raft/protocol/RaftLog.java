package raft.protocol;

import org.apache.log4j.Logger;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc
 */
public class RaftLog {

    private Logger log = Logger.getLogger(RaftLog.class);

    //已知的最大的已经被提交的日志条目的索引值
    private long commitIndex;
    //最后被应用到状态机的日志条目索引值
    private long lastApplied = 0;

}
