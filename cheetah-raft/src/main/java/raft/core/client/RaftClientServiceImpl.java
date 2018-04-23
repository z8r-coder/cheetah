package raft.core.client;

import org.apache.log4j.Logger;
import raft.core.RaftClientService;
import raft.core.RaftConsensusService;
import raft.model.BaseRequest;
import raft.protocol.request.CommandExecuteRequest;
import raft.protocol.request.GetLeaderRequest;
import raft.protocol.request.GetServerListRequest;
import raft.protocol.response.CommandExecuteResponse;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;
import rpc.client.SimpleClientRemoteProxy;
import rpc.wrapper.RpcConnectorWrapper;
import rpc.wrapper.connector.RpcServerSyncConnector;
import utils.Configuration;
import utils.DateUtil;
import utils.NetUtils;
import utils.ParseUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-04-11
 * @desc raft client connection local method, don't need to register
 */
public class RaftClientServiceImpl implements RaftClientService {

    private final static Logger logger = Logger.getLogger(RaftClientServiceImpl.class);

    private String localHost;

    private Configuration configuration;

    private Map<Integer, RpcConnectorWrapper> connectorCache;

    public RaftClientServiceImpl () {
        localHost = NetUtils.getLocalHost();
        configuration = new Configuration();
        connectorCache = new HashMap<>();
    }

    @Override
    public GetLeaderResponse getLeader() {
        //generate request
        GetLeaderRequest request = new GetLeaderRequest();
        request.setAddress(localHost,0,configuration.getRaftClusterHost(),
                Integer.parseInt(configuration.getRaftClusterPort()), ParseUtils.generateServerId(localHost,0));

        logger.info("client getLeader request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        return raftConsensusService.getLeader(request);
    }

    @Override
    public GetServerListResponse getServerList() {
        //generate request
        GetServerListRequest request = new GetServerListRequest();
        request.setAddress(localHost,0,configuration.getRaftClusterHost(),
                Integer.parseInt(configuration.getRaftClusterPort()), ParseUtils.generateServerId(localHost,0));

        logger.info("client getServerList request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        return raftConsensusService.getServerList(request);
    }

    @Override
    public String getValue(String key) {
        return null;
    }

    @Override
    public boolean set(String key, String value) {
        return false;
    }

    @Override
    public boolean set(String key, String value, int expTime) {
        return false;
    }

    @Override
    public boolean set(String key, String value, int expTime, DateUtil.TimeUnit timeUnit) {
        return false;
    }


    private RaftConsensusService getRaftConsensusService(BaseRequest request) {
        int remoteServerId = ParseUtils.generateServerId(request.getRemoteHost(), request.getRemotePort());
        RpcConnectorWrapper connector;

        if ((connector = connectorCache.get(remoteServerId)) == null) {
            //exist
            connector = new RpcServerSyncConnector(request.getRemoteHost(),
                    request.getRemotePort());
            connectorCache.put(remoteServerId, connector);
        }

        connector.startService();
        SimpleClientRemoteProxy proxy = connector.getProxy();
        return proxy.registerRemote(RaftConsensusService.class);
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
