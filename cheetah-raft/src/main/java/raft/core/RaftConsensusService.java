package raft.core;

import raft.protocol.request.AddRequest;
import raft.protocol.request.VotedRequest;
import raft.protocol.response.AddResponse;
import raft.protocol.response.VotedResponse;

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


}
