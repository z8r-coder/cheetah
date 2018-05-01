package raft.protocol.request;

import raft.model.BaseRequest;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-05-01
 * @desc
 */
public class RegisterServerRequest extends BaseRequest implements Serializable {
    private String newHost;
    private int newPort;

    public RegisterServerRequest (String newHost, int newPort) {
        this.newHost = newHost;
        this.newPort = newPort;
    }

    public String getNewHost() {
        return newHost;
    }

    public void setNewHost(String newHost) {
        this.newHost = newHost;
    }

    public int getNewPort() {
        return newPort;
    }

    public void setNewPort(int newPort) {
        this.newPort = newPort;
    }
}
