package utils.code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/2.
 */
public class ByteArrCode implements StoreCode<byte[]> {

    public void encode(ByteBuffer buffer, byte[] bytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    public byte[] decode(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }
}
