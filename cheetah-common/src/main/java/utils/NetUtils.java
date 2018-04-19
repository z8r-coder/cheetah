package utils;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc
 */
public class NetUtils {

    private final static Logger logger = Logger.getLogger(NetUtils.class);

    public static String getLocalHost () {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("ignore getLocalHost occurs ex:", e);
            return null;
        }
    }
}
