package rpc.serializer;

import rpc.exception.RpcException;
import rpc.utils.NioUtils;

import java.io.*;

/**
 * @Author:Roy
 * @Date: Created in 23:22 2017/11/7 0007
 */
public class JdkSerializer implements RpcSerializer {

    public byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(bos);
            stream.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            //使用zip压缩，缩小网络包
            return NioUtils.zip(bytes);
        } catch (IOException e) {
            throw new RpcException(e);
        } finally {
            try {
                bos.close();
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public Object deserialize(byte[] bytes) {
        //使用unzip解压
        byte[] unzip = NioUtils.unzip(bytes);
        ByteArrayInputStream bis = new ByteArrayInputStream(unzip);
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(bis);
            return stream.readObject();
        } catch (Exception e) {
            throw new RpcException(e);
        } finally {
            try {
                bis.close();
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
