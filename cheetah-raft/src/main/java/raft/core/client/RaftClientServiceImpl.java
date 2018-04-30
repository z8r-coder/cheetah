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
import utils.StringUtils;

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
    private RpcConnectorWrapper connector;
    private String remoteHost;
    private int remotePort;

    private CheetahAddress leaderAddress;

    public RaftClientServiceImpl (String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        localHost = NetUtils.getLocalHost();
        connector = new RpcServerSyncConnector(remoteHost, remotePort);
        connector.startService();
    }

    @Override
    public GetLeaderResponse getLeader() {
        //generate request
        GetLeaderRequest request = new GetLeaderRequest();
        RaftConsensusService raftConsensusService = setAddressAndGetRaftConsensusService(request);

        logger.info("client getLeader request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());
        GetLeaderResponse response = raftConsensusService.getLeader(request);
        if (response == null) {
            logger.warn("raft client getLeader response == null");
            return null;
        }
        leaderAddress = response.getCheetahAddress();
        return response;
    }

    @Override
    public GetServerListResponse getServerList() {
        //generate request
        GetServerListRequest request = new GetServerListRequest();
        RaftConsensusService raftConsensusService = setAddressAndGetRaftConsensusService(request);

        logger.info("client getServerList request ,remote host=" + request.getRemoteHost() +
        " ,remote port=" + request.getRemotePort());
        GetServerListResponse response = raftConsensusService.getServerList(request);
        if (response == null) {
            logger.warn("raft client getServerList response == null");
            return null;
        }
        leaderAddress = response.getLeaderAddress();
        return response;
    }

    @Override
    public String getValue(String key) {
        //generate request
        GetValueRequest request = new GetValueRequest(key);
        RaftConsensusService raftConsensusService = setAddressAndGetRaftConsensusService(request);
        logger.info("client get value request ,remote host=" + request.getRemoteHost() +
                " ,remote port=" + request.getRemotePort());

        GetValueResponse response = raftConsensusService.getValue(request);
        if (response == null) {
            logger.warn("raft client getValue response == null");
            return null;
        }
        leaderAddress = response.getLeaderAddress();
        return response.getValue();
    }

    @Override
    public String set(String command) {
        //generate request
        SetKVRequest request = new SetKVRequest(command);
        RaftConsensusService raftConsensusService = setAddressAndGetRaftConsensusService(request);
        logger.info("client set kv request ,remote host=" + request.getRemoteHost() +
                " ,remote port=" + request.getRemotePort());

        SetKVResponse response = raftConsensusService.setKV(request);
        if (response == null) {
            logger.warn("raft client set kv response == null");
            return null;
        }
        leaderAddress = response.getLeaderAddress();
        return response.getRespMessage();
    }

    /**
     * 更新地址，并重启，客户端缓存了集群leader的IP
     * @param request
     */
    private RaftConsensusService setAddressAndGetRaftConsensusService(BaseRequest request) {
        request.setLocalHost(localHost);
        request.setLocalPort(0);
        if (leaderAddress == null) {
            request.setRemoteHost(remoteHost);
            request.setRemotePort(remotePort);
        } else {
            if (!StringUtils.equals(leaderAddress.getHost(), remoteHost) ||
                    leaderAddress.getPort() != remotePort) {
                //cluster have changed the leader ,need to update and restart the connector
                remoteHost = leaderAddress.getHost();
                remotePort = leaderAddress.getPort();
                connector.stopService();
                connector = new RpcServerSyncConnector(remoteHost, remotePort);
                connector.startService();
                request.setRemoteHost(leaderAddress.getHost());
                request.setRemotePort(leaderAddress.getPort());
            }
        }
        SimpleClientRemoteProxy proxy = connector.getProxy();
        return proxy.registerRemote(RaftConsensusService.class);
    }
}
