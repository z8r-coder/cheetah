package raft.protocol;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-04-01
 * @desc log segment
 */
public class Segment {

    public static class Record{
        public long offset;
        public RaftLog.LogEntry logEntry;
        public Record(long offset, RaftLog.LogEntry logEntry) {
            this.offset = offset;
            this.logEntry = logEntry;
        }
    }

    private boolean canWrite;
    private String fileName;
    private long startIndex;
    private long endIndex;
    private RandomAccessFile randomAccessFile;
    private List<Record> entries = new ArrayList<Record>();

    public RaftLog.LogEntry getEntry(long index) {
        if (startIndex == 0 || endIndex == 0) {
            return null;
        }
        if (index < startIndex || index > endIndex) {
            return null;
        }
        int myIndex = (int) (index - startIndex);
        return entries.get(myIndex).logEntry;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public List<Record> getEntries() {
        return entries;
    }

    public void setEntries(List<Record> entries) {
        this.entries = entries;
    }
}
