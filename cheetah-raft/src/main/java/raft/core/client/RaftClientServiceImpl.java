package raft.core.client;

import constants.ErrorCodeEnum;
import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.core.RaftClientService;
import raft.core.RaftConsensusService;
import raft.model.BaseRequest;
import raft.protocol.request.GetLeaderRequest;
import raft.protocol.request.GetServerListRequest;
import raft.protocol.request.GetValueRequest;
import raft.protocol.request.SetKVRequest;
import raft.protocol.response.GetLeaderResponse;
import raft.protocol.response.GetServerListResponse;
import raft.protocol.response.GetValueResponse;
import raft.protocol.response.SetKVResponse;
import raft.utils.RaftUtils;
import rpc.client.SimpleClientRemoteProxy;
import rpc.exception.RpcException;
import rpc.wrapper.RpcConnectorWrapper;
import rpc.wrapper.connector.RpcServerSyncConnector;
import utils.Configuration;
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
    private Map<Long, RpcConnectorWrapper> connectorCache;

    private CheetahAddress leaderAddress;
    private Map<Long, String> initServers;

    public RaftClientServiceImpl () {
        localHost = NetUtils.getLocalHost();
        configuration = new Configuration();
        connectorCache = new HashMap<>();
        initServers = RaftUtils.getInitCacheServerList(configuration);
    }

    @Override
    public GetLeaderResponse getLeader() {
        //generate request
        GetLeaderRequest request = new GetLeaderRequest();
        setAddress(request);

        logger.info("client getLeader request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        GetLeaderResponse response = raftConsensusService.getLeader(request);
        leaderAddress = response.getCheetahAddress();
        return response;
    }

    @Override
    public GetServerListResponse getServerList() {
        //generate request
        GetServerListRequest request = new GetServerListRequest();
        setAddress(request);

        logger.info("client getServerList request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        GetServerListResponse response = raftConsensusService.getServerList(request);
        leaderAddress = response.getLeaderAddress();
        return response;
    }

    @Override
    public String getValue(String key) {
        //generate request
        GetValueRequest request = new GetValueRequest(key);
        setAddress(request);
        logger.info("client get value request ,remote host=" + request.getRemoteHost() +
                " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        GetValueResponse response = raftConsensusService.getValue(request);
        if (response == null) {
            logger.warn("response == null");
            return null;
        }
        leaderAddress = response.getLeaderAddress();
        return response.getValue();
    }

    @Override
    public String set(String command) {
        //generate request
        SetKVRequest request = new SetKVRequest(command);
        setAddress(request);
        logger.info("client set kv request ,remote host=" + request.getRemoteHost() +
                " ,remote port=" + request.getRemotePort());

        RaftConsensusService raftConsensusService = getRaftConsensusService(request);
        SetKVResponse response = raftConsensusService.setKV(request);
        leaderAddress = response.getLeaderAddress();
        return response.getRespMessage();
    }

    private RaftConsensusService getRaftConsensusService(BaseRequest request) {
        long remoteServerId = ParseUtils.generateServerId(request.getRemoteHost(), request.getRemotePort());
        RpcConnectorWrapper connector;

        if ((connector = connectorCache.get(remoteServerId)) == null) {
            //exist
            connector = new RpcServerSyncConnector(request.getRemoteHost(),
                    request.getRemotePort());
            connectorCache.put(remoteServerId, connector);
        }
        try {
            connector.startService();
        } catch (Exception ex) {
            //connect error retry
            if (ex instanceof RpcException &&
                    ((RpcException) ex).getErrorCode().equals(ErrorCodeEnum.RPC00020.getErrorCode())) {
                logger.error("client connect remote host=" + request.getRemoteHost() +
                " ,port=" + request.getRemotePort());
                connectorCache.remove(remoteServerId);
            }
        }

        SimpleClientRemoteProxy proxy = connector.getProxy();
        return proxy.registerRemote(RaftConsensusService.class);
    }

    private void setAddress(BaseRequest request) {
        request.setLocalHost(localHost);
        request.setLocalPort(0);
        if (leaderAddress == null) {
            request.setRemoteHost(configuration.getRaftClusterHost());
            request.setRemotePort(Integer.parseInt(configuration.getRaftClusterPort()));
        } else {
            request.setRemoteHost(leaderAddress.getHost());
            request.setRemotePort(leaderAddress.getPort());
        }
    }
}
