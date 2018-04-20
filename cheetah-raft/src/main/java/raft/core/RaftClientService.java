package raft.core;

import raft.protocol.request.CommandExecuteRequest;
import raft.protocol.response.CommandExecuteResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc raft client
 */
public interface RaftClientService {

    public GetLeaderResponse getLeader();

    public GetServerListResponse getServerList();

    public CommandExecuteResponse commandExec (CommandExecuteRequest request);
}
