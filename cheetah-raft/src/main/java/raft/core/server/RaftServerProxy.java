package raft.core.server;

import org.apache.log4j.Logger;
import rpc.nio.AbstractRpcNioSelector;
import rpc.registry.ServerProxy;
import utils.Configuration;

/**
 * @author ruanxin
 * @create 2018-03-06
 * @desc
 */
public class RaftServerProxy extends ServerProxy {
    private Logger logger = Logger.getLogger(RaftServerProxy.class);

    public RaftServerProxy() {
        super();
    }

    public RaftServerProxy(AbstractRpcNioSelector selector, Configuration configuration) {
        super(selector, configuration);
    }

    public void startService() {
        super.startService();

    }

    public void stopService() {
        super.stopService();
    }
}
