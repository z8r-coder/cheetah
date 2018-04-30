package raft;

import raft.core.ParserGateWayService;
import raft.core.RaftClientService;
import raft.core.client.RaftClientServiceImpl;
import raft.core.imp.ParserGateWayServiceImpl;
import utils.Configuration;

import java.util.Scanner;

/**
 * @author ruanxin
 * @create 2018-04-25
 * @desc
 */
public class CommandParserDemo {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        RaftClientService raftClientService = new RaftClientServiceImpl(configuration.getRaftClusterHost(),
                Integer.parseInt(configuration.getRaftClusterPort()));
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
