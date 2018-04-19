package raft;

import models.CheetahAddress;
import raft.core.rpc.RaftRpcServerAcceptor;
import utils.ParseUtils;

/**
 * @author ruanxin
 * @create 2018-04-19
 * @desc 单点多进程DEMO
 */
public class RaftServerMutilProcessStartDemo {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("this is args length == 0");
        }
        String address = args[0];
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
        RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(cheetahAddress.getHost(), cheetahAddress.getPort());
        acceptor.startService();
    }
}
