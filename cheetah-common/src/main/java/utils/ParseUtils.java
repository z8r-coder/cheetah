package utils;

import models.CheetahAddress;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc
 */
public class ParseUtils {

    private Logger logger = Logger.getLogger(ParseUtils.class);

    /**
     * parse address like 127.0.0.1:8080
     * @param address
     */
    public static CheetahAddress parseAddress (String address) {
        int index = address.indexOf(':');
        String host = address.substring(0, index);
        int port = Integer.parseInt(address.substring(index + 1, address.length()));

        CheetahAddress cheetahAddress = new CheetahAddress(host, port);
        return cheetahAddress;
    }

    public static List<CheetahAddress> parseListAddress (Set<String> addresses) {
        List<CheetahAddress> serverList = new ArrayList<CheetahAddress>();
        for (String address : addresses) {
            CheetahAddress cheetahAddress = parseAddress(address);
            serverList.add(cheetahAddress);
        }
        return serverList;
    }

    public static int generateServerId(String host, int port) {
        String preServerId = host.replaceAll("\\.", "");
        String strServerId = preServerId + port;
        int serverId = Integer.parseInt(strServerId);
        return serverId;
    }

    public static void main(String[] args) {
        CheetahAddress cheetahAddress = parseAddress("127.0.0.1:8080");
        System.out.println(cheetahAddress.getHost() + ":" + cheetahAddress.getPort());
    }
}
