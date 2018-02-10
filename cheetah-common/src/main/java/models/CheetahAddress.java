package models;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-10
 * @desc
 */
public class CheetahAddress implements Serializable {

    private String host;
    private int port;

    public CheetahAddress (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
