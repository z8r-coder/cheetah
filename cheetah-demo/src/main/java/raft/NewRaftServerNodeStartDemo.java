package raft;

import mock.RaftMock;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.core.rpc.RaftRpcServerAcceptor;
import raft.core.server.CheetahServer;
import utils.Configuration;
import utils.ParseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-05-05
 * @desc
 */
public class NewRaftServerNodeStartDemo {

    private final static Logger logger = Logger.getLogger(NewRaftServerNodeStartDemo.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("this is args length == 0");
        }
        String address = args[0];
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
        RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(RaftMock.rootPathMapping.get(cheetahAddress.getPort()),
                cheetahAddress.getHost(), cheetahAddress.getPort());
        CheetahServer cheetahServer = new CheetahServer(acceptor);
        cheetahServer.start();

        logger.info(cheetahAddress.getHost() + ":" + cheetahAddress.getPort() +
                " have started!");
    }
}
