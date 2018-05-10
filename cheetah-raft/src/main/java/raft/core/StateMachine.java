package raft.core;

/**
 * @author ruanxin
 * @create 2018-02-06
 * @desc state machine spi
 */
public interface StateMachine {

    /**
     * submit the log entries to state machine, only write
     */
    public void submit(byte[] data);

    /**
     * get data
     */
    public byte[] get(String key);

    /**
     * stop thread pool
     */
    public void stop();
}
