package rpc.demo.registry;

import rpc.registry.SimpleRegisterServerProxy;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class RegisterCenter {
    public static void main(String[] args) {
        SimpleRegisterServerProxy proxy = new SimpleRegisterServerProxy();
        proxy.startService();
    }
}
