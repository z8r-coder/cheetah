package models;

/**
 * @author ruanxin
 * @create 2018-04-02
 * @desc
 */
public class RaftIndexInfo {
    private long startIndex;
    private long endIndex;
    public RaftIndexInfo(long startIndex, long endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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
}
