package raft.core.client;

import org.apache.log4j.Logger;
import raft.core.RaftClientService;
import raft.protocol.request.CommandExecuteRequest;
import raft.protocol.request.GetLeaderRequest;
import raft.protocol.request.GetServerListRequest;
import raft.protocol.response.CommandExecuteResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;
import rpc.wrapper.RpcConnectorWrapper;
import rpc.wrapper.connector.RpcServerSyncConnector;
import utils.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ruanxin
 * @create 2018-04-11
 * @desc raft client connection local method, don't need to register
 */
public class RaftClientServiceImpl implements RaftClientService {

    private final static Logger logger = Logger.getLogger(RaftClientServiceImpl.class);

    private Configuration configuration = new Configuration();

    @Override
    public GetLeaderResponse getLeader(GetLeaderRequest request) {
        logger.info("getLeader request, remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());
        RpcConnectorWrapper connector = new RpcServerSyncConnector(configuration.getRaftClusterHost(),
                Integer.parseInt(configuration.getRaftClusterPort()));
        connector.startService();
//        GetLeaderResponse response =   new GetLeaderResponse()
        return null;
    }

    @Override
    public GetServerListResponse getServerList(GetServerListRequest request) {
        return null;
    }

    @Override
    public CommandExecuteResponse commandExec(CommandExecuteRequest request) {
        return null;
    }

    public static void main(String[] args) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println(inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
