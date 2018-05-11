package raft.protocol.request;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-05-11
 * @desc
 */
public class TestGetValueRequest extends BaseRequest implements Serializable {
    private String key;

    public TestGetValueRequest (String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
