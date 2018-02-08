package rpc.net;

import rpc.RpcRegisterCenter;
import rpc.RpcService;

/**
 * @Author:Roy
 * @Date: Created in 0:15 2017/10/15 0015
 */
public abstract class AbstractRpcNetworkBase implements RpcService {
    /**
     * ip 服务端绑定ip,客户端连接ip
     */
    private String host;
    /**
     * 服务端绑定端口,客户端连接端口
     */
    private int port;

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
