package rpc.demo.nio;

import org.apache.log4j.Logger;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;

/**
 * @Author:Roy
 * @Date: Created in 17:27 2017/12/2 0002
 */
public class RpcNioClient {
    private static Logger logger = Logger.getLogger(RpcNioClient.class);

    public static void main(String args[]) {
        String host = "127.0.0.1";
        int port = 4332;
        AbstractRpcConnector connector = new RpcNioConnector(null);
        connector.setHost(host);
        connector.setPort(port);


    }
}
