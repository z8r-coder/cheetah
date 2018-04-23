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
     * get value
     */
    public GetValueResponse getValue(GetValueRequest request);

    /**
     * set key value
     */
    public SetKVResponse setKV(SetKVRequest request);
}
