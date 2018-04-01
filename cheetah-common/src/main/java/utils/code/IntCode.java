package utils.code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/2.
 */
public class IntCode implements StoreCode<Integer> {
    public void encode(ByteBuffer buffer, Integer value) {
        buffer.putInt(value);
    }

    public Integer decode(ByteBuffer buffer) {
        return buffer.getInt();
    }
}
