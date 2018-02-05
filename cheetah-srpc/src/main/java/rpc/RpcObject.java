package rpc;

import rpc.constants.RpcType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:Roy
 * @Date: Created in 22:52 2017/10/14 0014
 */
public class RpcObject {
    /**
     * Rpc类型， 协议字段
     */
    private RpcType type;

    /**
     * 请求线程Id， 协议字段
     */
    private long threadId;

    /**
     * 请求线程类seq, 用于标记请求发送 协议字段
     */
    private int index;

    /**
     * data数据结构长度  协议字段
     */
    private int length;

    /**
     * 请求body 协议字段
     */
    private byte[] data = new byte[0];

    /**
     * 请求发送方
     */
    private String host;

    /**
     * 请求发送发
     */
    private int port;

    private ConcurrentHashMap<String, Object> rpcContext;

    public RpcObject() {

    }

    public RpcObject(int type,int index,int len,byte[] data){
        this.type = RpcType.getByType(type);
        this.index = index;
        this.length = len;
        this.data = data;
        this.threadId = Thread.currentThread().getId();
    }

    @Override
    public String toString() {
        return "RpcObject [type=" + type + ", threadId=" + threadId
                + ", index=" + index + ", length=" + length + ", host=" + host
                + ", port=" + port + "]";
    }

    public RpcType getType() {
        return type;
    }

    public void setType(RpcType type) {
        this.type = type;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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

    public ConcurrentHashMap<String, Object> getRpcContext() {
        return rpcContext;
    }

    public void setRpcContext(ConcurrentHashMap<String, Object> rpcContext) {
        this.rpcContext = rpcContext;
    }
}
