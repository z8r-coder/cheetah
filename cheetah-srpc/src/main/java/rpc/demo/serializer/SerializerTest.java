package rpc.demo.serializer;


import rpc.serializer.JdkSerializer;

/**
 * @Author:Roy
 * @Date: Created in 23:39 2017/11/7 0007
 */
public class SerializerTest {
    public static void main(String args[]) {
        SerializerBean serializerBean = new SerializerBean();
        JdkSerializer jdkSerializer = new JdkSerializer();
        byte[] bytesTest = jdkSerializer.serialize(serializerBean);
        SerializerBean serializerBean1 = (SerializerBean) jdkSerializer.deserialize(bytesTest);
        System.out.println(serializerBean1.getTest1() + ":" + serializerBean1.getTest2());
    }
}
