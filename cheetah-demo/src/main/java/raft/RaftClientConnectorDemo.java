package raft;

import models.CheetahAddress;
import raft.core.ParserGateWayService;
import raft.core.RaftClientService;
import raft.core.client.RaftClientServiceImpl;
import raft.core.imp.ParserGateWayServiceImpl;
import utils.Configuration;
import utils.ParseUtils;

import java.util.Scanner;

/**
 * @author ruanxin
 * @create 2018-05-11
 * @desc 作为测试工具类，直接获得某台服务器上的数据，查看信息是否同步
 */
public class RaftClientConnectorDemo {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("this is args length == 0");
        }
        String addrress = args[0];
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(addrress);
        RaftClientService raftClientService = new RaftClientServiceImpl(cheetahAddress.getHost(),
                cheetahAddress.getPort());
        ParserGateWayService parserGateWayService = new ParserGateWayServiceImpl(raftClientService);
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.length() == 0) {
                System.out.print(">");
                continue;
            }
            String result = parserGateWayService.parse(line);
            if ("exit".equals(result)) {
                break;
            } else {
                System.out.println(result);
            }
            System.out.print(">");
        }
    }
}
