package raft.core.imp;

import raft.core.ParserGateWayService;
import raft.core.RaftClientService;
import raft.protocol.request.CommandExecuteRequest;
import raft.protocol.request.CommandParseRequest;
import raft.protocol.response.CommandParseResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class ParserGateWayServiceImpl implements ParserGateWayService {

    private final static String WRONG = "wrong syntax!";
    private int serverId;
    private RaftClientService raftClientService;

    public ParserGateWayServiceImpl (int serverId, RaftClientService raftClientService) {
        this.serverId = serverId;
        this.raftClientService = raftClientService;
    }

    @Override
    public CommandParseResponse parse(CommandParseRequest request) {
        String command = request.getCommand();
        String[] commandArr = command.split("\\ ");
        CommandParseResponse response = new CommandParseResponse(serverId, WRONG);
        if (commandArr.length < 2) {
            return response;
        }
        if (commandArr[0].equals("get")) {
            if (commandArr[1].equals("leader")) {
                //command get leader
                if (commandArr.length > 2) {
                    return response;
                }
                GetLeaderResponse getLeaderResponse = raftClientService.getLeader();
                int leaderId = getLeaderResponse.getLeaderId();
                response.setResult(String.valueOf(leaderId));
                return response;
            } else if (commandArr[1].equals("servers")) {
                //command get servers
                if (commandArr.length > 2) {
                    return response;
                }
                GetServerListResponse getServerListResponse = raftClientService.getServerList();
                Map<Integer, String> serverList = getServerListResponse.getServerList();
                StringBuilder sb = new StringBuilder();
                for (String value : serverList.values()) {
                    sb.append(value + ",");
                }
                response.setResult(sb.toString());
            } else {
                if (commandArr.length > 2) {
                    return response;
                }
            }
        }
        return null;
    }
}
