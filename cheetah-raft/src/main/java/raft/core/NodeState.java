package raft.core;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc
 */
public enum NodeState {
    FOLLOWER,
    CANDIDATE,
    LEADER;
}
