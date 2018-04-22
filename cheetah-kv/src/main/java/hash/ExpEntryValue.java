package hash;

/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc
 */
public class ExpEntryValue {

    private String expTime;
    private byte[] data;

    /**
     * if expTime == null means the data will be stored forever.
     * @param expTime
     * @param data
     */
    public ExpEntryValue (String expTime, byte[] data) {
        this.expTime = expTime;
        this.data = data;
    }

    public String getExpTime() {
        return expTime;
    }

    public void setExpTime(String expTime) {
        this.expTime = expTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
