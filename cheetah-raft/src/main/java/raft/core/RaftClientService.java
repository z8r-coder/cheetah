package raft.core;

import raft.protocol.*;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc raft client
 */
public interface RaftClientService {

    public GetLeaderResponse getLeader(GetLeaderRequest request);

    public GetServerListResponse getServerList(GetServerListRequest request);

    public CommandExecuteResponse commandExec (CommandExecuteRequest request);
}
