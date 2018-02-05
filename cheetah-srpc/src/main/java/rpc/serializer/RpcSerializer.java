package rpc.serializer;

/**
 * @Author:Roy
 * @Date: Created in 23:20 2017/11/7 0007
 */
public interface RpcSerializer {
    /**
     * 序列化
     * @param obj
     * @return
     */
    public byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @return
     */
    public Object deserialize(byte[] bytes);
}
