package raft.protocol.response;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 返回
 */
public class RaftResponse implements Serializable{
    //标注来自哪台服务器
    private long serverId;

    public RaftResponse (long serverId) {
        this.serverId = serverId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
