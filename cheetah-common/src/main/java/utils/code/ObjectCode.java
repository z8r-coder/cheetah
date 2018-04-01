package utils.code;

import java.nio.ByteBuffer;

/**
 * Created by rx on 2017/9/2.
 */
public class ObjectCode implements StoreCode<Object> {

    public void encode(ByteBuffer buffer, Object value) {
        CodeUtils.encode(value, buffer);
    }

    public Object decode(ByteBuffer buffer) {
        Object obj = CodeUtils.decode(buffer);

        return obj;
    }
}
