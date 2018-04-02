package raft.protocol;

import org.apache.log4j.Logger;
import raft.core.RaftLogDataRoute;
import raft.utils.RaftUtils;
import sun.rmi.runtime.Log;
import utils.ParseUtils;
import utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

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
    //max log size per file
    private int maxFileLogSize;
    //log entry directory
    private String logEntryDir;
    //log meta data directory
    protected String metaDataDir;
    //total size info
    private volatile long totalSize;
    //data route
    private RaftLogDataRoute logDataRoute;
    //meta's file name
    private String metaFileName;
    //meta's file full name
    private String metaFileFullName;
    //meta data map
    private Map<Long, SegmentMetaData> logMetaDataMap = new TreeMap<Long, SegmentMetaData>();
    //segment cache LRU
    private Map<Long, Segment> segmentCache = new LinkedHashMap<Long, Segment>(16,0.75f,true);

    private GlobleMetaData globleMetaData;

    public RaftLog(int maxFileLogSize, String logEntryDir, String metaFileName) {
        this.logDataRoute = new RaftLogDataRoute(this);
        this.maxFileLogSize = maxFileLogSize;
        this.logEntryDir = logEntryDir + File.separator + "raft_log";
        this.metaDataDir = logEntryDir + File.separator + "raft_meta";
        this.metaFileFullName = metaDataDir + File.separator + metaFileName + ".meta";
        this.metaFileName = metaFileName;

        initDirAndFileAndReadMetaData();
    }

    public RaftLog(long commitIndex, long lastApplied, int lastLogTerm, int lastLogIndex) {
        this.commitIndex = commitIndex;
        this.lastApplied = lastApplied;
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
    }

    /**
     * init meta data dir, create meta file
     * log entry data dir
     */
    private void initDirAndFileAndReadMetaData () {
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
            RandomAccessFile randomAccessFile = null;
            try {
                //first create
                fileMetaDir.createNewFile();
                //init info
                randomAccessFile = RaftUtils.openFile(metaDataDir, metaFileName, "w");
                randomAccessFile.writeLong(0l);
                randomAccessFile.writeLong(0l);
                randomAccessFile.writeInt(0);
            } catch (IOException e) {
                logger.error("create new file occur ex=", e);
            } finally {
                RaftUtils.closeFile(randomAccessFile);
            }
        } else {
            //exist, read meta data from this
            RandomAccessFile randomAccessFile = RaftUtils.openFile(metaDataDir, metaFileName, "rw");
            readGlobleMetaData(randomAccessFile);
        }
        readSegmentData();
    }

    private void writeGlobleMetaData (RandomAccessFile randomAccessFile,
                                      GlobleMetaData globleMetaData) {
        try {
            randomAccessFile.writeLong(globleMetaData.startIndex);
            randomAccessFile.writeLong(globleMetaData.lastIndex);
            randomAccessFile.writeInt(globleMetaData.nameLength);
            randomAccessFile.write(globleMetaData.lastSegmentLogName.getBytes());
        } catch (IOException e) {
            logger.error("write globle meta data occurs ex", e);
        } finally {
            RaftUtils.closeFile(randomAccessFile);
        }
    }

    private void readGlobleMetaData(RandomAccessFile randomAccessFile) {
        try {
            long startIndex = randomAccessFile.readLong();
            long lastIndex = randomAccessFile.readLong();
            int segmentNameLength = randomAccessFile.readInt();
            byte[] segmentNameByteArr = new byte[segmentNameLength];
            randomAccessFile.read(segmentNameByteArr);
            String segmentName = new String(segmentNameByteArr);
            globleMetaData = new GlobleMetaData(segmentName,lastIndex, startIndex);
        } catch (IOException e) {
            logger.error("read globle meta data occurs ex",e);
            throw new RuntimeException("read globle meta data occurs ex");
        } finally {
            RaftUtils.closeFile(randomAccessFile);
        }
    }

    /**
     * load segment
     * @param segment
     * @param dataLength
     * @return
     */
    public List<LogEntry> loadSegment(Segment segment, long dataLength) {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        for (long i = 0l;i < dataLength;i++) {
            LogEntry logEntry = new LogEntry(segment);
            logEntry.readFrom();
            entries.add(logEntry);
        }
        return entries;
    }

    /**
     * lru
     * @param segment
     */
    public void cacheSegment (Segment segment) {
        if (segmentCache.size() < 16) {
            segmentCache.put(segment.getStartIndex(), segment);
        } else {

        }
    }

    public int getLogEntryTerm (long logIndex) throws Exception {
        LogEntry logEntry = getEntry(logIndex);
        if (logEntry == null) {
            return 0;
        }
        return logEntry.getTerm();
    }

    public LogEntry getEntry(long logIndex) throws Exception {
        if (logIndex < globleMetaData.startIndex ||
                logIndex > globleMetaData.lastIndex) {
            return null;
        }
        return logDataRoute.findLogEntryByIndex(logIndex,logMetaDataMap, logEntryDir);
    }

    public static class GlobleMetaData {
        private String lastSegmentLogName;
        private int nameLength;
        private long lastIndex;
        private long startIndex;
        public GlobleMetaData (String lastSegmentLogName,
                               long lastIndex, long startIndex) {
            this.startIndex = startIndex;
            this.lastIndex = lastIndex;
            this.lastSegmentLogName = lastSegmentLogName;
            this.nameLength = lastSegmentLogName.getBytes().length;
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

    public void readSegmentData(){
        List<String> fileNameList = null;
        try {
            fileNameList = RaftUtils.getSortedFilesInDir(logEntryDir, logEntryDir);
        } catch (IOException e) {
            logger.error("read Segment Data occurs error!", e);
        }
        for (String fileName : fileNameList) {
            String[] fileMeta = ParseUtils.parseByPoint(fileName);
            if (!StringUtils.equals(fileMeta[1], "rl")) {
                continue;
            }
            String[] metaArray = fileMeta[0].split("-");
            SegmentMetaData metaData = new SegmentMetaData(Long.parseLong(metaArray[0]),
                    Long.parseLong(metaArray[1]), fileName);
            logMetaDataMap.put(Long.parseLong(metaArray[0]), metaData);
        }
    }


    public static class LogEntry {
        private int term;
        private long index;
        private int dataLength;
        private Segment segment;
        private byte[] data;

        public LogEntry(Segment segment) {
            term = 0;
            index = 0l;
            data = new byte[1];
            dataLength = 1;
            this.segment = segment;
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
            } finally {
                RaftUtils.closeFile(randomAccessFile);
            }
        }

        public void readFrom() {
            RandomAccessFile randomAccessFile = segment.getRandomAccessFile();
            try {
                term = randomAccessFile.readInt();
                index = randomAccessFile.readLong();
                dataLength = randomAccessFile.readInt();
                data = new byte[dataLength];
                randomAccessFile.read(data);
            } catch (IOException e) {
                throw new RuntimeException("read from file error!");
            } finally {
                RaftUtils.closeFile(randomAccessFile);
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
        TreeMap<Long, String> map = new TreeMap<Long, String>();
        map.put(1l,"1");
        map.put(2l, "2");
        map.put(5l, "5");
        map.put(4l, "4");
        for (Long key : map.keySet()) {
            System.out.println(key);
        }
        System.out.println(String.format("%s-%s.rl",100,120));
    }
}
