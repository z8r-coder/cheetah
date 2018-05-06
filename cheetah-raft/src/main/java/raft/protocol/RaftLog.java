package raft.protocol;

import cache.LRUCache;
import com.google.protobuf.ByteString;
import models.RaftIndexInfo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import raft.core.RaftLogDataRoute;
import raft.utils.RaftUtils;
import utils.ParseUtils;
import utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc 日志
 */
public class RaftLog {

    private Logger logger = Logger.getLogger(RaftLog.class);

    private ReentrantLock lock = new ReentrantLock(true);

    //候选人最后日志条目的任期号
    private int lastLogTerm;
    //候选人的最后日志条目的索引值,默认-1,第一次会+1,所以会默认为0
    private long lastLogIndex;
    //已知的最大的已经被提交的日志条目的索引值
    private long commitIndex;
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
    private TreeMap<Long, SegmentMetaData> logMetaDataMap = new TreeMap<>();
    //segment cache LRU
    private LRUCache<Long, Segment> segmentCache = new LRUCache<>(16, 0.75f, true, 16);

    private GlobleMetaData globleMetaData;

    public RaftLog(int maxFileLogSize, String logEntryDir, String metaFileName) {
        this.logDataRoute = new RaftLogDataRoute(this);
        this.maxFileLogSize = maxFileLogSize;
        this.logEntryDir = logEntryDir + File.separator + "raft_log";
        this.metaDataDir = logEntryDir + File.separator + "raft_meta";
        this.metaFileFullName = metaDataDir + File.separator + metaFileName + ".meta";
        this.metaFileName = metaFileName + ".meta";

        initDirAndFileAndReadMetaData();
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
                fileMeta.createNewFile();
                //init info
                randomAccessFile = RaftUtils.openFile(metaDataDir, metaFileName, "rw");
                randomAccessFile.writeLong(0l);//start index
                randomAccessFile.writeLong(0l);//last index
                randomAccessFile.writeLong(0l);//last apply
                randomAccessFile.writeInt(0);//segment name length
                globleMetaData = new GlobleMetaData(0,0);
            } catch (IOException e) {
                logger.error("create new file occur ex=", e);
            } finally {
                RaftUtils.closeFile(randomAccessFile);
            }
        } else {
            //read every segment data
            readSegmentData();
            //exist, read globle meta data from this
            RandomAccessFile randomAccessFile = RaftUtils.openFile(metaDataDir, metaFileName, "rw");
            readGlobleMetaData(randomAccessFile);
        }
    }

    /**
     * 是否存在log entry
     * @return
     */
    public boolean existLogEntry () {
        if (logMetaDataMap.size() == 0) {
            return false;
        }
        return true;
    }

    public void loadLastSegment(String lastSegmentName) {
        RaftIndexInfo raftIndexInfo = ParseUtils.parseIndexInfoByFileName(lastSegmentName);
        lastLogIndex = raftIndexInfo.getEndIndex();
        Segment segment = loadSegment(raftIndexInfo.getStartIndex());
        LogEntry logEntry = segment.getEntry(lastLogIndex);
        lastLogTerm = logEntry.getTerm();
        commitIndex = lastLogIndex;

    }

    private void readGlobleMetaData(RandomAccessFile randomAccessFile) {
        try {
            long startIndex = randomAccessFile.readLong();
            long lastIndex = randomAccessFile.readLong();
            lastApplied = randomAccessFile.readLong();
            int segmentNameLength = randomAccessFile.readInt();
            if (segmentNameLength == 0) {
                //init
                commitIndex = -1;
                lastLogIndex = 0;
                lastLogTerm = 0;
                lastApplied = 0;
                globleMetaData = new GlobleMetaData(startIndex, lastIndex);
                return;
            }
            byte[] segmentNameByteArr = new byte[segmentNameLength];
            randomAccessFile.read(segmentNameByteArr);
            String segmentName = new String(segmentNameByteArr);

            //load log entry meta info
            List<String> fileNameList = RaftUtils.getSortedFilesInDir(logEntryDir,logEntryDir);

            TreeMap<Long, SegmentInfo> segmentInfoMap = new TreeMap<Long, SegmentInfo>();

            for (int i = 0; i < fileNameList.size(); i++) {
                String fileName = fileNameList.get(i);
                RaftIndexInfo raftIndexInfo = ParseUtils.parseIndexInfoByFileName(fileName);
                boolean isCanWrite = randomAccessFile.readBoolean();
                long dataNum = randomAccessFile.readLong();
                SegmentInfo segmentInfo = new SegmentInfo(isCanWrite, dataNum);
                segmentInfoMap.put(raftIndexInfo.getStartIndex(), segmentInfo);
            }
            globleMetaData = new GlobleMetaData(segmentName,lastIndex, startIndex, segmentInfoMap);
            //update meta data
            loadLastSegment(segmentName);
        } catch (IOException e) {
            logger.error("read globle meta data occurs ex",e);
        } finally {
            RaftUtils.closeFile(randomAccessFile);
        }
    }

    private void writeGlobleMetaData (RandomAccessFile randomAccessFile,
                                      GlobleMetaData globleMetaData) {
        try {
            randomAccessFile.writeLong(globleMetaData.startIndex);
            randomAccessFile.writeLong(globleMetaData.lastIndex);
            randomAccessFile.writeLong(lastApplied);
            randomAccessFile.writeInt(globleMetaData.nameLength);
            randomAccessFile.write(globleMetaData.lastSegmentLogName.getBytes());
            for (Map.Entry<Long, SegmentInfo> entry : globleMetaData.segmentInfoMap.entrySet()) {
                SegmentInfo segmentInfo = entry.getValue();
                randomAccessFile.writeBoolean(segmentInfo.isCanWrite);
                randomAccessFile.writeLong(segmentInfo.dataNum);
            }
        } catch (IOException e) {
            logger.error("write globle meta data occurs ex", e);
        } finally {
            RaftUtils.closeFile(randomAccessFile);
        }
    }

    /**
     * load segment
     * @return
     */
    public Segment loadSegment(long realIndex) {
        if (segmentCache.get(realIndex) != null) {
            return segmentCache.get(realIndex);
        }
        SegmentMetaData segmentMetaData = logMetaDataMap.get(realIndex);
        if (segmentMetaData == null) {
            return null;
        }
        SegmentInfo segmentInfo = globleMetaData.segmentInfoMap.get(realIndex);
        RandomAccessFile randomAccessFile = RaftUtils.openFile(logEntryDir, segmentMetaData.fileName, "rw");
        Segment segment = new Segment(segmentMetaData.fileName, segmentMetaData.startIndex,
                segmentMetaData.endIndex, randomAccessFile, segmentInfo.isCanWrite);

        try {
            List<Segment.Record> entries = new ArrayList<Segment.Record>();
            for (long i = 0l; i < segmentInfo.dataNum;i++) {
                LogEntry logEntry = new LogEntry();
                Segment.Record record = new Segment.Record(randomAccessFile.getFilePointer(), logEntry);
                logEntry.readFrom(segment);
                entries.add(record);
            }
            segment.setEntries(entries);
            segment.setFileSize(randomAccessFile.length());
            //cache
            cacheSegment(segment);
        } catch (Exception ex) {
//            ex.printStackTrace();
            throw new RuntimeException("load segment occurs ex!", ex);
        }

        return segment;
    }

    /**
     * lru
     * @param segment
     */
    private void cacheSegment (Segment segment) {
        segmentCache.put(segment.getStartIndex(), segment);
    }

    public void truncateSuffix (long newEndIndex) {
        if (newEndIndex > getLastLogIndex()) {
            return;
        }
        logger.info("truncate log from old index " +  getLastLogIndex() + " to new end index " + newEndIndex);
        while (!logMetaDataMap.isEmpty()) {
            long lastSegmentIndex = logMetaDataMap.lastKey();
            Segment segment = loadSegment(lastSegmentIndex);
            try {
                if (newEndIndex == segment.getEndIndex()) {
                    LogEntry logEntry = segment.getEntry(newEndIndex);
                    //update protocol data
                    updateProtocolData(logEntry.getTerm(), newEndIndex);
                    //update globle info
                    globleMetaData.setLastSegmentLogName(segment.getFileName());
                    globleMetaData.lastIndex = newEndIndex;
                    break;
                } else if (newEndIndex < segment.getStartIndex()) {
                    //last segment
                    totalSize -= segment.getFileSize();
                    segment.getRandomAccessFile().close();
                    String fullFileName = logEntryDir + File.separator + segment.getFileName();
                    FileUtils.forceDelete(new File(fullFileName));
                    logMetaDataMap.remove(lastSegmentIndex);
                    segmentCache.remove(lastSegmentIndex);
                    globleMetaData.segmentInfoMap.remove(lastSegmentIndex);
                } else if (newEndIndex < segment.getEndIndex()){
                    int index = (int)(newEndIndex + 1 - segment.getStartIndex());
                    segment.setEndIndex(newEndIndex);
                    long newFileSize = segment.getEntries().get(index).offset;
                    segment.setFileSize(newFileSize);
                    segment.getEntries().removeAll(
                            segment.getEntries().subList(index, segment.getEntries().size()));
                    FileChannel fileChannel = segment.getRandomAccessFile().getChannel();
                    fileChannel.truncate(segment.getFileSize());
                    fileChannel.close();
                    segment.getRandomAccessFile().close();
                    String oldFullFileName = logEntryDir + File.separator + segment.getFileName();
                    String newFileName = String.format("%d-%d",
                            segment.getStartIndex(), segment.getEndIndex());
                    segment.setFileName(newFileName);
                    String newFullFileName = logEntryDir + File.separator + newFileName;
                    new File(oldFullFileName).renameTo(new File(newFullFileName));
                    segment.setRandomAccessFile(RaftUtils.openFile(logEntryDir, segment.getFileName(), "rw"));
                    if (newFileSize < maxFileLogSize) {
                        segment.setCanWrite(true);
                    }
                    //update log meta data
                    SegmentMetaData segmentMetaData = logMetaDataMap.get(segment.getStartIndex());
                    segmentMetaData.fileName = newFileName;
                    segmentMetaData.startIndex = segment.getStartIndex();
                    segmentMetaData.endIndex = segment.getEndIndex();
                    //update globle
                    SegmentInfo segmentInfo = globleMetaData.segmentInfoMap.get(segment.getStartIndex());
                    segmentInfo.dataNum = index;
                    segmentInfo.isCanWrite = true;
                }
            } catch (Exception ex) {
                logger.warn("io exception ", ex);
            }
        }
    }

    /**
     * add log entry
     * @param logEntries
     */
    public long append (List<LogEntry> logEntries) {
        long newLastIndexLog = lastLogIndex;
        for (LogEntry logEntry : logEntries) {
            if (logMetaDataMap.size() != 0) {
                newLastIndexLog++;
            }
            int entrySize = logEntry.getSerializedSize();
            int segmentSize = logMetaDataMap.size();
            boolean isNeedNewSegmentFile = false;
            Segment segment = null;
            try {
                if (segmentSize == 0) {
                    isNeedNewSegmentFile = true;
                } else {
                    segment = logDataRoute.findSegmentByIndex(lastLogIndex, logMetaDataMap);
                    if (!segment.isCanWrite()) {
                        isNeedNewSegmentFile = true;
                    } else if (segment.getFileSize() + entrySize >= maxFileLogSize) {
                        isNeedNewSegmentFile = true;
                        //close the last segment file
                        RaftUtils.closeFile(segment.getRandomAccessFile());
                        segment.setCanWrite(false);
                    }
                }
                Segment newSegment;
                //new segment file
                if (isNeedNewSegmentFile) {
                    String newFileName = String.format("%d-%d.rl",newLastIndexLog, newLastIndexLog);
                    RandomAccessFile randomAccessFile = RaftUtils.openFile(logEntryDir, newFileName, "rw");
                    newSegment = new Segment(newFileName, newLastIndexLog, newLastIndexLog,
                            randomAccessFile, true);
                    globleMetaData.setLastSegmentLogName(newFileName);
                    SegmentInfo segmentInfo = new SegmentInfo(true, 0);
                    globleMetaData.segmentInfoMap.put(newLastIndexLog, segmentInfo);
                } else {
                    newSegment = segment;
                }
                totalSize += entrySize;
                newSegment.getEntries().add(new Segment.Record(newSegment.getRandomAccessFile().getFilePointer(),
                        logEntry));
                newSegment.setEndIndex(newLastIndexLog);
                //write protocol to
                logEntry.writeTo(newSegment);
                newSegment.setFileSize(newSegment.getRandomAccessFile().length());
                //update file name
                String oldFullFileName = logEntryDir + File.separator + newSegment.getFileName();
                String newFileName = String.format("%d-%d.rl",newSegment.getStartIndex(), newSegment.getEndIndex());
                newSegment.setFileName(newFileName);
                String newFullFileName = logEntryDir + File.separator + newFileName;
                new File(oldFullFileName).renameTo(new File(newFullFileName));
                //update protocol data
                updateProtocolData(logEntry.getTerm(), logEntry.getIndex());
                if (logMetaDataMap.get(newSegment.getStartIndex()) == null) {
                    SegmentMetaData segmentMetaData = new SegmentMetaData(newSegment.getStartIndex(),
                            newSegment.getEndIndex(), newFileName);
                    logMetaDataMap.put(newSegment.getStartIndex(), segmentMetaData);
                } else {
                    SegmentMetaData segmentMetaData = logMetaDataMap.get(newSegment.getStartIndex());
                    segmentMetaData.endIndex = newLastIndexLog;
                    segmentMetaData.fileName = newFileName;
                }

                // TODO: 2018/4/8 全局元信息是否必须维护
                //update meta data
                globleMetaData.lastIndex = newLastIndexLog;
                globleMetaData.setLastSegmentLogName(newFileName);
                SegmentInfo segmentInfo = globleMetaData.segmentInfoMap.get(newSegment.getStartIndex());
                segmentInfo.dataNum++;

                //cache
                cacheSegment(newSegment);
            } catch (IOException ex) {
                throw new RuntimeException("append raft log entry occurs ex:" + ex);
            }
        }
        RandomAccessFile randomAccessFile = RaftUtils.openFile(metaDataDir, metaFileName, "rw");
        writeGlobleMetaData(randomAccessFile, globleMetaData);
        return newLastIndexLog;
    }


    public void updateProtocolData (int lastLogTerm, long lastLogIndex) {
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
    }

    public int getLogEntryTerm (long logIndex) {
        LogEntry logEntry = getEntry(logIndex);
        if (logEntry == null) {
            //todo 若还不存在日志条目，则默认是第一届
            return 1;
        }
        return logEntry.getTerm();
    }

    public List<Segment.Record> getEntries (long logIndex) {
        return logDataRoute.findLogEntriesByIndex(logIndex, logMetaDataMap);
    }

    public LogEntry getEntry(long logIndex) {
        if (logIndex < globleMetaData.startIndex ||
                logIndex > globleMetaData.lastIndex) {
            return null;
        }
        return logDataRoute.findLogEntryByIndex(logIndex,logMetaDataMap);
    }

    public static class GlobleMetaData {
        private String lastSegmentLogName;
        private int nameLength;
        private volatile long lastIndex;
        private long startIndex;
        private TreeMap<Long, SegmentInfo> segmentInfoMap;

        public GlobleMetaData (long startIndex,long lastIndex) {
            this.lastIndex = lastIndex;
            this.startIndex = startIndex;
            segmentInfoMap = new TreeMap<Long, SegmentInfo>();
        }

        public GlobleMetaData (String lastSegmentLogName,
                               long lastIndex, long startIndex,
                               TreeMap<Long, SegmentInfo> segmentInfoMap) {
            this.startIndex = startIndex;
            this.lastIndex = lastIndex;
            this.lastSegmentLogName = lastSegmentLogName;
            this.nameLength = lastSegmentLogName.getBytes().length;
            this.segmentInfoMap = segmentInfoMap;
        }

        public void setLastSegmentLogName (String lastSegmentLogName) {
            this.lastSegmentLogName = lastSegmentLogName;
            this.nameLength = lastSegmentLogName.getBytes().length;
        }

        @Override
        public String toString() {
            return "startIndex=" + startIndex +
                    " ,lastIndex=" + lastIndex +
                    " ,lastSegmentLogName=" + lastSegmentLogName +
                    " ,nameLength=" + nameLength +
                    " ,segmentInfoMap Size=" + segmentInfoMap.size();
        }

        public String segmentInfoToString () {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Long, SegmentInfo> entry : segmentInfoMap.entrySet()) {
                sb.append("startIndex=" + entry.getKey() +
                " ,segment data num=" + entry.getValue().dataNum +
                " ,segment canWrite=" + entry.getValue().isCanWrite +
                "\n");
            }
            return sb.toString();
        }
    }

    public static class SegmentInfo {
        public boolean isCanWrite;
        public long dataNum;

        public SegmentInfo (boolean isCanWrite, long dataNum) {
            this.isCanWrite = isCanWrite;
            this.dataNum = dataNum;
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

    public static class LogEntry implements Serializable {
        private int term;
        private long index;
        private int dataLength;
        private byte[] data;

        public LogEntry() {
            term = 0;
            index = 0l;
            data = ByteString.EMPTY.toByteArray();
            dataLength = 0;
        }
        public LogEntry(int term, long index,
                        byte[] data) {
            this.data = data;
            this.term = term;
            this.index = index;
            dataLength = data.length;
        }

        public void writeTo(Segment segment) {
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

        public void readFrom(Segment segment) {
            RandomAccessFile randomAccessFile = segment.getRandomAccessFile();
            try {
                term = randomAccessFile.readInt();
                index = randomAccessFile.readLong();
                dataLength = randomAccessFile.readInt();
                data = new byte[dataLength];
                randomAccessFile.read(data);
            } catch (IOException e) {
                e.printStackTrace();
//                throw new RuntimeException("read from file error!");
            }
        }

        public int getSerializedSize() {
            return 16 + data.length;
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

        @Override
        public String toString() {
            String str = new String(data);
            return "term=" + term + ", index=" + index +
                    " ,data=" + str;
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

    public int getMaxFileLogSize() {
        return maxFileLogSize;
    }

    public void setMaxFileLogSize(int maxFileLogSize) {
        this.maxFileLogSize = maxFileLogSize;
    }

    public String getLogEntryDir() {
        return logEntryDir;
    }

    public void setLogEntryDir(String logEntryDir) {
        this.logEntryDir = logEntryDir;
    }

    public String getMetaDataDir() {
        return metaDataDir;
    }

    public void setMetaDataDir(String metaDataDir) {
        this.metaDataDir = metaDataDir;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public RaftLogDataRoute getLogDataRoute() {
        return logDataRoute;
    }

    public void setLogDataRoute(RaftLogDataRoute logDataRoute) {
        this.logDataRoute = logDataRoute;
    }

    public String getMetaFileName() {
        return metaFileName;
    }

    public void setMetaFileName(String metaFileName) {
        this.metaFileName = metaFileName;
    }

    public String getMetaFileFullName() {
        return metaFileFullName;
    }

    public void setMetaFileFullName(String metaFileFullName) {
        this.metaFileFullName = metaFileFullName;
    }

    public TreeMap<Long, SegmentMetaData> getLogMetaDataMap() {
        return logMetaDataMap;
    }

    public void setLogMetaDataMap(TreeMap<Long, SegmentMetaData> logMetaDataMap) {
        this.logMetaDataMap = logMetaDataMap;
    }

    public LRUCache<Long, Segment> getSegmentCache() {
        return segmentCache;
    }

    public void setSegmentCache(LRUCache<Long, Segment> segmentCache) {
        this.segmentCache = segmentCache;
    }

    public GlobleMetaData getGlobleMetaData() {
        return globleMetaData;
    }

    public void setGlobleMetaData(GlobleMetaData globleMetaData) {
        this.globleMetaData = globleMetaData;
    }

    public static void main(String[] args) {
        //读文件指针和写文件指针是不一样的两个指针
//        File file = new File("/Users/ruanxin/IdeaProjects/cheetah" + File.separator + "raft_log");
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        TreeMap<Long, String> map = new TreeMap<Long, String>();
//        map.put(1l,"1");
//        map.put(2l, "2");
//        map.put(5l, "5");
//        map.put(4l, "4");
//        for (Long key : map.keySet()) {
//            System.out.println(key);
//        }
//        System.out.println(String.format("%s-%s.rl",100,120));
        RandomAccessFile randomAccessFile = RaftUtils.openFile("/Users/ruanxin/IdeaProjects/cheetah/raft", "4.txt", "rw");
        String test = "test";
//        RandomAccessFile randomAccessFile1 = RaftUtils.openFile("/Users/ruanxin/IdeaProjects/cheetah/raft", "1.txt", "rw");
        try {
            randomAccessFile.write(test.getBytes());
            byte[] testByte = new byte[test.getBytes().length];
            randomAccessFile.read(testByte);
            String tt = new String(testByte);
            System.out.println(tt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            RaftUtils.closeFile(randomAccessFile);
        }
//        RandomAccessFile randomAccessFile = RaftUtils.openFile("/Users/ruanxin/IdeaProjects/cheetah/raft", "5.txt", "rw");
//
//        new File("/Users/ruanxin/IdeaProjects/cheetah/raft/5.txt").renameTo(new File("/Users/ruanxin/IdeaProjects/cheetah/raft/6.txt"));
//        for (int i = 0; i < 0;i++) {
//            System.out.println(i);
//        }
    }
}
