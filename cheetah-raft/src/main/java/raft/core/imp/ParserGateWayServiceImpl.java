package raft.core.imp;

import raft.core.ParserGateWayService;
import raft.core.RaftClientService;
import raft.core.client.RaftClientServiceImpl;
import raft.protocol.response.CommandParseResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;

import java.util.Map;
import java.util.Scanner;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc client
 */
public class ParserGateWayServiceImpl implements ParserGateWayService {

    private final static String WRONG = "wrong syntax!";
    private final static String EXIT = "exit";
    private RaftClientService raftClientService;

    public ParserGateWayServiceImpl (RaftClientService raftClientService) {
        this.raftClientService = raftClientService;
    }

    @Override
    public String parse(String command) {
        String[] commandArr = command.split("\\ ");
        String result = WRONG;
        CommandParseResponse response = new CommandParseResponse(WRONG);
        if (commandArr.length < 2) {
            return WRONG;
        }
        if (commandArr[0].equals("get")) {
            if (commandArr[1].equals("leader")) {
                //command get leader
                if (commandArr.length > 2) {
                    return WRONG;
                }
                GetLeaderResponse getLeaderResponse = raftClientService.getLeader();
                long leaderId = getLeaderResponse.getLeaderId();
                result = String.valueOf(leaderId);
            } else if (commandArr[1].equals("servers")) {
                //command get servers
                if (commandArr.length > 2) {
                    return WRONG;
                }
                GetServerListResponse getServerListResponse = raftClientService.getServerList();
                Map<Long, String> serverList = getServerListResponse.getServerList();
                StringBuilder sb = new StringBuilder();
                for (String value : serverList.values()) {
                    sb.append(value + ",");
                }
                result = sb.toString();
            } else {
                // command get value
                if (commandArr.length > 2) {
                    return WRONG;
                }
                String value = raftClientService.getValue(commandArr[1]);
                result = value;
            }
        } else if (commandArr[0].equals("set")) {
            if (commandArr.length >= 3 && commandArr.length <= 5) {
                String setRes = raftClientService.set(command);
                return setRes;
            }
            response.setResult(WRONG);
        } else if (commandArr[0].equals("exit")) {
            if (commandArr.length >= 2) {
                return WRONG;
            }
            result = EXIT;
        }
        return result;
    }
}
