package utils.code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/2.
 */
public class LongCode implements StoreCode<Long>{


    public void encode(ByteBuffer buffer, Long value) {
        buffer.putLong(value);
    }

    public Long decode(ByteBuffer buffer) {
        return buffer.getLong();
    }
}
