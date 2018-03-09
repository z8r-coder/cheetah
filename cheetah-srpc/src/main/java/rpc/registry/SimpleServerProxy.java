package rpc.registry;

import rpc.nio.AbstractRpcNioSelector;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-03-09
 * @desc
 */
public class SimpleServerProxy extends AbstractServerProxy {

    public SimpleServerProxy() {
        super();
    }
    public SimpleServerProxy(AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector, configuration);
    }

    @Override
    public void startService() {
        super.startService();
    }

    @Override
    public void stopService() {
        super.stopService();
    }

    protected void register() {
        System.out.println("this is address: " + getHost() + ":" + getPort());
    }
}
