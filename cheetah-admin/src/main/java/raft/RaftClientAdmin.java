package raft;

import raft.core.RaftClientService;
import raft.core.client.RaftClientServiceImpl;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;
import utils.Configuration;

import java.util.Map;
import java.util.Scanner;

/**
 * @author ruanxin
 * @create 2018-04-20
 * @desc
 */
public class RaftClientAdmin {

    private RaftClientService raftClientService;

    public RaftClientAdmin (RaftClientService raftClientService) {
        this.raftClientService = raftClientService;
    }

    public void scanInputAndParse () {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.length() == 0) {
                System.out.print(">");
                continue;
            }
            String[] lineArr = line.split("\\ ");
            if (lineArr[0].equals("get")) {
                if (lineArr[1].equals("leader")) {
                    if (lineArr.length > 2) {
                        System.out.println("wrong syntax!");
                    } else {
                        //execute command
                        GetLeaderResponse response = raftClientService.getLeader();
                        if (response == null) {
                            System.out.println("request time out!");
                        } else {
                            System.out.println(response.getLeaderId());
                        }
                    }
                } else if (lineArr[1].equals("servers")) {
                    if (lineArr.length > 2) {
                        System.out.println("wrong syntax!");
                    } else {
                        //execute command
                        GetServerListResponse response = raftClientService.getServerList();
                        if (response == null) {
                            System.out.println("request time out!");
                        } else {
                            Map<Long, String> serverList = response.getServerList();
                            for (String value : serverList.values()) {
                                System.out.println(value);
                            }
                        }
                    }
                } else {
                    //get value from kv system

                }
            } else if (lineArr[0].equals("set")) {
                //set value from kv system
            } else if (lineArr[0].equals("exit")) {
                break;
            } else {
                System.out.println("wrong syntax!");
            }
            System.out.print(">");

        }

    }
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        RaftClientService raftClientService = new RaftClientServiceImpl(configuration.getRaftClusterHost(),
                Integer.parseInt(configuration.getRaftClusterPort()));
        RaftClientAdmin raftClientAdmin = new RaftClientAdmin(raftClientService);
        raftClientAdmin.scanInputAndParse();
    }
}
