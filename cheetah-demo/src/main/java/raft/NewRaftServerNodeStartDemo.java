package raft;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.core.rpc.RaftRpcServerAcceptor;
import raft.core.server.CheetahServer;
import utils.ParseUtils;

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
        RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(cheetahAddress.getHost(), cheetahAddress.getPort());
        CheetahServer cheetahServer = new CheetahServer(acceptor);
        cheetahServer.start();

        logger.info(cheetahAddress.getHost() + ":" + cheetahAddress.getPort() +
                " have started!");
    }
}
