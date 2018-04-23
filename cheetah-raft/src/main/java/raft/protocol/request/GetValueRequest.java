package raft.protocol.request;

import raft.model.BaseRequest;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class GetValueRequest extends BaseRequest {
    private String key;
    private int serverId;

    public GetValueRequest (String key, int serverId) {
        this.key = key;
        this.serverId = serverId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
