package rpc.demo.registry;

import rpc.registry.ServerProxy;
import rpc.utils.RpcUtils;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class Server1 {
    public static void main(String[] args) {
        ServerProxy proxy = new ServerProxy();
        RpcUtils.setAddress("127.0.0.1", 4333, proxy);
        proxy.startService();
    }
}
