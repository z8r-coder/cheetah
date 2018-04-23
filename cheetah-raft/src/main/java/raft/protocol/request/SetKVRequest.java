package raft.protocol.request;

import raft.model.BaseRequest;
import utils.DateUtil;

/**
 * @author ruanxin
 * @create 2018-04-23
 * @desc
 */
public class SetKVRequest extends BaseRequest {

    private String key;
    private String value;
    private DateUtil.TimeUnit timeUnit = DateUtil.TimeUnit.mm;
    private int expTime = -1;
    private int serverId;

    public SetKVRequest (String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setAddress(String localHost, int localPort,
                           String remoteHost, int remotePort, int serverId) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.serverId = serverId;
    }

    public SetKVRequest (String key, String value, DateUtil.TimeUnit timeUnit, int expTime) {
        this.key = key;
        this.value = value;
        this.timeUnit = timeUnit;
        this.expTime = expTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DateUtil.TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(DateUtil.TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getExpTime() {
        return expTime;
    }

    public void setExpTime(int expTime) {
        this.expTime = expTime;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
