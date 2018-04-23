package raft.core;

import raft.protocol.request.CommandExecuteRequest;
import raft.protocol.response.CommandExecuteResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;
import utils.DateUtil;

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
     * @param key
     * @return
     */
    public String getValue(String key);

    /**
     * set kv without exp time
     */
    public boolean set(String key, String value);

    /**
     * set kv with exp time time unit:default mm
     */
    public boolean set(String key, String value, int expTime);

    /**
     * set kv with exp time and time unit
     */
    public boolean set(String key, String value, int expTime, DateUtil.TimeUnit timeUnit);
}
