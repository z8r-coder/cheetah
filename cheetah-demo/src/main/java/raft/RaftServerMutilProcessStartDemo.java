package raft;

import mock.RaftMock;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.core.rpc.RaftRpcServerAcceptor;
import raft.core.server.CheetahServer;
import utils.ParseUtils;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc 单点多进程DEMO
 */
public class RaftServerMutilProcessStartDemo {

    private final static Logger logger = Logger.getLogger(RaftServerMutilProcessStartDemo.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("this is args length == 0");
        }
        String address = args[0];
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
        RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(RaftMock.rootPathMapping.get(cheetahAddress.getPort()),
                cheetahAddress.getHost(), cheetahAddress.getPort());
        CheetahServer cheetahServer = new CheetahServer(acceptor);
        cheetahServer.clusterInit();

        logger.info(cheetahAddress.getHost() + ":" + cheetahAddress.getPort() +
        " have started!");
    }
}
