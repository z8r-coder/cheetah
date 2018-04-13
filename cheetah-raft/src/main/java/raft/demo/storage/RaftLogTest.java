package raft.demo.storage;

import raft.constants.RaftOptions;
import raft.core.RaftLogDataRoute;
import raft.protocol.RaftLog;
import raft.protocol.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-04-11
 * @desc
 */
public class RaftLogTest {
    private RaftLog raftLog;
    private RaftLogDataRoute raftLogDataRoute;
    private RaftOptions raftOptions = new RaftOptions();
    private static final String LOGPATH = "/Users/ruanxin/IdeaProjects/cheetah/raft";

    public RaftLogTest () {
        raftLog = new RaftLog(64, LOGPATH, "raft_meta");
        raftLogDataRoute = new RaftLogDataRoute(raftLog);
    }

    public void appendEntries() {
        String set1 = "set A = B";
        String set2 = "set A = C";
        String set3 = "set A = D";
        String set4 = "set A = E";
        String set5 = "set A = F";
        List<RaftLog.LogEntry> entries = new ArrayList<RaftLog.LogEntry>();
        RaftLog.LogEntry logEntry1 = new RaftLog.LogEntry(0,0l, set1.getBytes());
        RaftLog.LogEntry logEntry2 = new RaftLog.LogEntry(0,1l,set2.getBytes());
        RaftLog.LogEntry logEntry3 = new RaftLog.LogEntry(0, 2l,set3.getBytes());
        RaftLog.LogEntry logEntry4 = new RaftLog.LogEntry(0,3l,set4.getBytes());
        RaftLog.LogEntry logEntry5 = new RaftLog.LogEntry(0, 4l,set5.getBytes());
        entries.add(logEntry1);
        entries.add(logEntry2);
        entries.add(logEntry3);
        entries.add(logEntry4);
        entries.add(logEntry5);

        raftLog.append(entries);
    }

    public void getEntry (long index) {
        RaftLog.LogEntry logEntry = raftLog.getEntry(index);
        System.out.println(logEntry.toString());
    }

    public void getEntries (long index) {
        List<Segment.Record> records = raftLog.getEntries(index);
        for (Segment.Record record : records) {
            System.out.println(record.logEntry.toString());
        }
    }

    public void getGlobleMetaInfo () {
        RaftLog.GlobleMetaData globleMetaData = raftLog.getGlobleMetaData();
        System.out.println(globleMetaData.toString());
    }

    public void getGlobleSegmentInfo () {
        RaftLog.GlobleMetaData globleMetaData = raftLog.getGlobleMetaData();
        System.out.println(globleMetaData.segmentInfoToString());
    }

    public void getSegmentInfo (long realIndex) {
        Segment segment = raftLog.loadSegment(realIndex);
        System.out.println(segment.toString());
    }

    public static void main(String[] args) {
        RaftLogTest raftLogTest = new RaftLogTest();
//        raftLogTest.appendEntries();
//        raftLogTest.getGlobleMetaInfo();
//        raftLogTest.getGlobleSegmentInfo();
//        raftLogTest.getSegmentInfo(2);
        raftLogTest.getEntry(4);
//        raftLogTest.getEntries(3);
    }
}
