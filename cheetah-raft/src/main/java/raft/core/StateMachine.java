package raft.core;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc state machine spi
 */
public interface StateMachine {

    /**
     * submit the log entries to state machine
     */
    public void submit(byte[] data);
}
