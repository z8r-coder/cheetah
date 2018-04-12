package raft.core;

import models.RaftIndexInfo;
import raft.protocol.RaftLog;
import raft.protocol.Segment;
import raft.utils.RaftUtils;
import utils.ParseUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-01
 * @desc 日志数据路由,no status
 */
public class RaftLogDataRoute {

    private RaftLog raftLog;

    public RaftLogDataRoute (RaftLog raftLog) {
        this.raftLog = raftLog;
    }

    public RaftLog.LogEntry findLogEntryByIndex (long index,
                                                 Map<Long, RaftLog.SegmentMetaData> segmentMetaDataMap) {
        Segment segment = findSegmentByIndex(index, segmentMetaDataMap);
        return segment.getEntry(index);
    }

    public List<Segment.Record> findLogEntriesByIndex (long index,
                                                       Map<Long, RaftLog.SegmentMetaData> segmentMetaDataMap) {
        Segment segment = findSegmentByIndex(index, segmentMetaDataMap);
        return segment.getEntries();
    }

    public Segment findSegmentByIndex(long index, Map<Long, RaftLog.SegmentMetaData> segmentMetaDataMap) {
        long realIndex = 0;
        for (long startIndex : segmentMetaDataMap.keySet()) {
            if (startIndex > index) {
                break;
            }
            realIndex = startIndex;
        }
        Segment segment = raftLog.loadSegment(realIndex);
        return segment;
    }

    public static void main(String[] args) {
        File file = new File("/Users/ruanxin/IdeaProjects/cheetah/raft/3.txt");
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.writeLong(1l);
            randomAccessFile.writeLong(2l);
            byte[] strByte = "testtest".getBytes();
            randomAccessFile.writeInt(strByte.length);
            randomAccessFile.write(strByte);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            System.out.println(randomAccessFile.readLong());
            System.out.println(randomAccessFile.readLong());
            int lenth = randomAccessFile.readInt();
            byte[] buf = new byte[lenth];
            randomAccessFile.read(buf);
            String test = new String(buf);
            System.out.println(test);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
