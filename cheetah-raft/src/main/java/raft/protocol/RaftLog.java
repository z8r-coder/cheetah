package raft.protocol;

import org.apache.log4j.Logger;
import raft.core.RaftLogDataRoute;
import raft.utils.RaftUtils;
import utils.ParseUtils;
import utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc 日志
 */
public class RaftLog {

    private Logger logger = Logger.getLogger(RaftLog.class);

    //候选人最后日志条目的任期号
    private int lastLogTerm;
    //候选人的最后日志条目的索引值
    private long lastLogIndex;
    //已知的最大的已经被提交的日志条目的索引值
    private long commitIndex = 0;
    //最后被应用到状态机的日志条目索引值
    private long lastApplied = 0;
    //log entry
    private Map<Long, LogEntry> logEntries = new TreeMap<Long, LogEntry>();
    //max log size per file
    private int maxFileLogSize;
    //log entry directory
    private String logEntryDir;
    //log meta data directory
    private String metaDataDir;
    //total size info
    private volatile long totalSize;
    //data route
    private RaftLogDataRoute logDataRoute;
    //meta's file name
    private String metaFileName;
    //meta's file full name
    private String metaFileFullName;
    //meta data map
    private Map<String, SegmentMetaData> logMetaDataMap = new TreeMap<String, SegmentMetaData>();

    public RaftLog(int maxFileLogSize, String logEntryDir, String metaFileName) {
        this.logDataRoute = new RaftLogDataRoute();
        this.maxFileLogSize = maxFileLogSize;
        this.logEntryDir = logEntryDir + File.separator + "raft_log";
        this.metaDataDir = logEntryDir + File.separator + "raft_meta";
        this.metaFileFullName = metaDataDir + File.separator + metaFileName + ".meta";

        this.metaFileName = metaFileName;
        File fileEntryDir = new File(logEntryDir);
        if (!fileEntryDir.exists()) {
            fileEntryDir.mkdir();
        }
        File fileMetaDir = new File(metaDataDir);
        if (!fileMetaDir.exists()) {
            fileMetaDir.mkdir();
        }
        File fileMeta = new File(metaFileFullName);
        if (!fileMeta.exists()) {
            try {
                fileMetaDir.createNewFile();
            } catch (IOException e) {
                logger.error("create new file occur ex=", e);
            }
        }
    }


    public static class GlobleMetaData {
        //last segment log
        private String lastSegmentLogName;
        //last index
        private long lastIndex;
        public GlobleMetaData (String lastSegmentLogName, long lastIndex) {
            this.lastIndex = lastIndex;
            this.lastSegmentLogName = lastSegmentLogName;
        }

        public void reset() {

        }
    }

    public static class SegmentMetaData {
        public long startIndex;
        public long endIndex;
        public String fileName;

        public SegmentMetaData(long startIndex, long endIndex, String fileName) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.fileName = fileName;
        }
    }

    public void readMetaData() throws IOException {
        List<String> fileNameList = RaftUtils.getSortedFilesInDir(logEntryDir, logEntryDir);
        for (String fileName : fileNameList) {
            String[] fileMeta = ParseUtils.parseByPoint(fileName);
            if (!StringUtils.equals(fileMeta[1], "rl")) {
                continue;
            }
            String[] metaArray = fileMeta[0].split("-");
            SegmentMetaData metaData = new SegmentMetaData(Long.parseLong(metaArray[0]),
                    Long.parseLong(metaArray[1]), fileName);
            logMetaDataMap.put(fileName, metaData);
        }

//        RaftUtils
    }

    public static class LogEntry {
        private int term;
        private long index;
        private int dataLength;
        private Segment segment;
        private byte[] data;

        public LogEntry() {
            term = 0;
            index = 0l;
            data = new byte[1];
            dataLength = 1;
        }
        public LogEntry(int term, long index,
                        byte[] data, Segment segment) {
            this.data = data;
            this.term = term;
            this.index = index;
            dataLength = data.length;
            this.segment = segment;
        }

        public void writeTo() {
            RandomAccessFile randomAccessFile = segment.getRandomAccessFile();
            try {
                randomAccessFile.writeInt(term);
                randomAccessFile.writeLong(index);
                randomAccessFile.writeInt(dataLength);
                randomAccessFile.write(data);
            } catch (IOException e) {
                throw new RuntimeException("write to file error!");
            }
        }

        public LogEntry readFrom() {
            RandomAccessFile randomAccessFile = segment.getRandomAccessFile();
            try {
                int term = randomAccessFile.readInt();
                long index = randomAccessFile.readLong();
                int dataLength = randomAccessFile.readInt();
                byte[] data = new byte[dataLength];
                randomAccessFile.read(data);
                LogEntry logEntry = new LogEntry(term, index, data,segment);
                return logEntry;
            } catch (IOException e) {
                throw new RuntimeException("read from file error!");
            }
        }

        public int getTerm() {
            return term;
        }

        public void setTerm(int term) {
            this.term = term;
        }

        public long getIndex() {
            return index;
        }

        public void setIndex(long index) {
            this.index = index;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getDataLength() {
            return dataLength;
        }

        public void setDataLength(int dataLength) {
            this.dataLength = dataLength;
        }
    }
    public RaftLog(long commitIndex, long lastApplied, int lastLogTerm, int lastLogIndex) {
        this.commitIndex = commitIndex;
        this.lastApplied = lastApplied;
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
    }

    public int getLogEntryTerm (long logIndex) {
        LogEntry logEntry = getEntry(logIndex);
        if (logEntry == null) {
            return 0;
        }
        return logEntry.getTerm();
    }

    public LogEntry getEntry(long logIndex) {
        return logEntries.get(logIndex);
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(long commitIndex) {
        this.commitIndex = commitIndex;
    }

    public long getLastApplied() {
        return lastApplied;
    }

    public void setLastApplied(long lastApplied) {
        this.lastApplied = lastApplied;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public static void main(String[] args) {
//        File file = new File("/Users/ruanxin/IdeaProjects/cheetah" + File.separator + "raft_log");
//        if (!file.exists()) {
//            file.mkdir();
//        }
        System.out.println(String.format("%s-%s.rl",100,120));
    }
}
