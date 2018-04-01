package utils.code;

import java.nio.ByteBuffer;

/**
 * 编码
 * Created by rx on 2017/9/2.
 */
public interface StoreCode<T> {
    /**
     * 编码
     * @param buffer
     * @param value
     */
    void encode(ByteBuffer buffer, T value);

    /**
     * 解码
     * @param buffer
     * @return
     */
    T decode(ByteBuffer buffer);
}
