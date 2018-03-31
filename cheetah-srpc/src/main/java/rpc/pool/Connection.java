package rpc.pool;

import rpc.net.AbstractRpcConnector;

/**
 * @author ruanxin
 * @create 2018-03-31
 * @desc abstract 连接
 */
public class Connection {
    private AbstractRpcConnector delegateCon;
    private int idleCon;
}
