package raft.demo.storage;

import constants.Globle;
import raft.constants.RaftOptions;
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
    private RaftOptions raftOptions = new RaftOptions();
    private static final String LOGPATH = "/Users/ruanxin/IdeaProjects/cheetah/raft";

    public RaftLogTest () {
        raftLog = new RaftLog(256, LOGPATH, "raft_meta");
    }

    public void appendEntries() {
        String set1 = "set A = B";
        String set2 = "set A = C";
        String set3 = "set A = D";
        List<RaftLog.LogEntry> entries = new ArrayList<RaftLog.LogEntry>();
        RaftLog.LogEntry logEntry1 = new RaftLog.LogEntry(0,0l, set1.getBytes());
        RaftLog.LogEntry logEntry2 = new RaftLog.LogEntry(0,1l,set2.getBytes());
        RaftLog.LogEntry logEntry3 = new RaftLog.LogEntry(0, 2l,set3.getBytes());
        entries.add(logEntry1);
        entries.add(logEntry2);
        entries.add(logEntry3);

        raftLog.append(entries);
    }

    public void getEntry () {
        RaftLog.LogEntry logEntry = raftLog.getEntry(2);
        System.out.println(logEntry.toString());
    }
    public void getEntries () {
        List<Segment.Record> records = raftLog.getEntries(0);
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

    public void getSegmentInfo () {
        Segment segment = raftLog.loadSegment(0);
        System.out.println(segment.toString());
    }

    public static void main(String[] args) {
        RaftLogTest raftLogTest = new RaftLogTest();
        raftLogTest.appendEntries();
    }
}
