package raft.demo.storage;

import raft.constants.RaftOptions;
import raft.protocol.RaftLog;

/**
 * @author ruanxin
 * @create 2018-04-11
 * @desc
 */
public class RaftLogTest {
    private RaftLog raftLog;
    private RaftOptions raftOptions;
    private static final String LOGPATH = "/Users/ruanxin/IdeaProjects/cheetah/raft";

    public RaftLogTest () {
        RaftLog raftLog = new RaftLog(raftOptions.getMaxLogSizePerFile(), LOGPATH, "raft_meta");
    }
    
}
