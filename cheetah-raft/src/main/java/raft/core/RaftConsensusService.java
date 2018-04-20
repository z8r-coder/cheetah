package raft.core;

import raft.protocol.request.*;
import raft.protocol.response.*;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 一致性算法 sync
 */
public interface RaftConsensusService {

    /**
     * leader election
     */
    public VotedResponse leaderElection(VotedRequest request);

    /**
     * reset timeout
     */
    public void resetTimeOut();

    /**
     * append entry
     */
    public AddResponse appendEntries (AddRequest request);

    /**
     * get leader
     */
    public GetLeaderResponse getLeader (GetLeaderRequest request);

    /**
     * get server list
     */
    public GetServerListResponse getServerList (GetServerListRequest request);

    /**
     * command exec
     */
    public CommandExecuteResponse clientCommandExec (CommandExecuteRequest request);
}
