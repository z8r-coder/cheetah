package rpc.utils;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author:Roy
 * @Date: Created in 14:33 2017/10/15 0015
 */
public class NioUtils {

    private final static Logger logger = Logger.getLogger(NioUtils.class);

    public final static int RPC_PROTOCOL_HEAD_LEN = 20;

    public static byte[] zip(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gos = null;
            try {
                gos = new GZIPOutputStream(bos);
                gos.write(bytes);
                //gis.close()需要放在生成字节数组之前，不然字节可能未解压完成
                gos.close();
                return bos.toByteArray();
            } catch (IOException e) {
                logger.error("NioUtils occurs ERROR: ", e);
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return new byte[0];
    }

    public static byte[] unzip(byte[] bytes) {
        GZIPInputStream gis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] buff = new byte[512];
            int read = gis.read(buff);
            while (read > 0) {
                bos.write(buff, 0, read);
                read = gis.read(buff);
            }
            //gis.close()需要放在生成字节数组之前，不然字节可能未解压完成
            gis.close();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error("NioUtils occurs ERROR: ", e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                //ignore
            }
        }
        return new byte[0];
    }
}
