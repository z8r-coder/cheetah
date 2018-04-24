package raft.core;

import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc raft client
 */
public interface RaftClientService {

    /**
     * get raft cluster leader
     * @return
     */
    public GetLeaderResponse getLeader();

    /**
     * get raft cluster server list
     * @return
     */
    public GetServerListResponse getServerList();

    /**
     * kv get value by key

     */
    public String getValue(String key);

    /**
     * set kv
     */
    public String set(String command);
}
