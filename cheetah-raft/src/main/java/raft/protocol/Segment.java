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

    private boolean canWrite;
    private String fileName;
    private long startIndex;
    private long endIndex;
    private long fileSize;
    private RandomAccessFile randomAccessFile;
    private List<RaftLog.LogEntry> entries = new ArrayList<RaftLog.LogEntry>();

    public Segment(String fileName, long startIndex, long endIndex,
                   RandomAccessFile randomAccessFile, boolean canWrite) {
        this.canWrite = canWrite;
        this.fileName = fileName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.randomAccessFile = randomAccessFile;
    }

    public RaftLog.LogEntry getEntry(long index) {
        if (startIndex == 0 || endIndex == 0) {
            return null;
        }
        if (index < startIndex || index > endIndex) {
            return null;
        }
        int myIndex = (int) (index - startIndex);
        return entries.get(myIndex);
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

    public List<RaftLog.LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<RaftLog.LogEntry> entries) {
        this.entries = entries;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
