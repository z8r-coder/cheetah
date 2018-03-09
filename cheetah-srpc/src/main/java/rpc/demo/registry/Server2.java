package rpc.demo.registry;

import rpc.registry.SimpleServerProxy;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class Server2 {
    public static void  main(String[] args) {
        SimpleServerProxy proxy = new SimpleServerProxy();
        RpcUtils.setAddress("127.0.0.1", 4332, proxy);
        proxy.startService();
    }
}
