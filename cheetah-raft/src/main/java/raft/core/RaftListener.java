package raft.core;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 监听心跳
 */
public interface RaftListener {
    /**
     * 监听
     */
    public void onListen();
}
