package utils;

import models.CheetahAddress;
import models.RaftIndexInfo;
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

    /**
     * parse filename,example:test.txt => [test][txt], only allow one point
     * @param fileName
     * @return
     */
    public static String[] parseByPoint(String fileName) {
        String fileMeta[] = fileName.split("\\.");
        if (fileMeta.length > 2) {
            throw new IllegalArgumentException("more than one point is found in this file name");
        }
        return fileMeta;
    }

    /**
     * parse file name to get the start index and end index info
     * @param fileName
     * @return
     */
    public static RaftIndexInfo parseIndexInfoByFileName(String fileName) {
        String[] fileMeta = parseByPoint(fileName);
        String[] indexInfo = fileMeta[0].split("-");
        RaftIndexInfo raftIndexInfo = new RaftIndexInfo(Long.parseLong(indexInfo[0]),
                Long.parseLong(indexInfo[1]));
        return raftIndexInfo;
    }

    public static int generateServerId(String host, int port) {
        String preServerId = host.replaceAll("\\.", "");
        String strServerId = preServerId + port;
        int serverId = Integer.parseInt(strServerId);
        return serverId;
    }

    public static void main(String[] args) {
//        CheetahAddress cheetahAddress = parseAddress("127.0.0.1:8080");
//        System.out.println(cheetahAddress.getHost() + ":" + cheetahAddress.getPort());
        System.out.println(ParseUtils.parseByPoint("test.tt")[1]);
    }
}
