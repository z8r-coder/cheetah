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

    public static class Record {
        public long offset;
        public RaftLog.LogEntry logEntry;
        public Record (long offset, RaftLog.LogEntry logEntry) {
            this.logEntry = logEntry;
            this.offset = offset;
        }
    }

    private boolean canWrite;
    private String fileName;
    private long startIndex;
    private long endIndex;
    private long fileSize;
    private RandomAccessFile randomAccessFile;
    private List<Record> entries = new ArrayList<Record>();

    public Segment(String fileName, long startIndex, long endIndex,
                   RandomAccessFile randomAccessFile, boolean canWrite) {
        this.canWrite = canWrite;
        this.fileName = fileName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.randomAccessFile = randomAccessFile;
    }

    public RaftLog.LogEntry getEntry(long index) {
        if (index < startIndex || index > endIndex) {
            return null;
        }
        int myIndex = (int) (index - startIndex);
        return entries.get(myIndex).logEntry;
    }

    @Override
    public String toString() {
        return "fileName=" + fileName +
                " ,startIndex=" + startIndex +
                " ,endIndex=" + endIndex +
                " ,canWrite=" + canWrite +
                " ,fileSize=" + fileSize;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
