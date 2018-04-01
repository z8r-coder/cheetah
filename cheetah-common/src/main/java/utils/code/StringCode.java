package utils.code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/3.
 */
public class StringCode implements StoreCode<String> {

    public void encode(ByteBuffer buffer, String value) {
        buffer.putInt(value.length());
        buffer.put(value.getBytes());
    }

    public String decode(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }
}
