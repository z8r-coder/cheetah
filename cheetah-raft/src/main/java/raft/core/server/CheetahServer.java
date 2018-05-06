package raft.core.server;

import models.CheetahAddress;
import org.apache.log4j.Logger;
import raft.core.RaftConsensusService;
import raft.core.rpc.RaftRpcServerAcceptor;
import raft.protocol.request.RegisterServerRequest;
import raft.protocol.response.RegisterServerResponse;
import raft.utils.RaftUtils;
import rpc.client.SimpleClientRemoteProxy;
import rpc.wrapper.RpcConnectorWrapper;
import rpc.wrapper.connector.RpcServerSyncConnector;
import utils.Configuration;
import utils.ParseUtils;
import utils.StringUtils;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-05-03
 * @desc cheetah server
 */
public class CheetahServer {
    private final static Logger logger = Logger.getLogger(CheetahServer.class);

    private RaftRpcServerAcceptor acceptor;
    private Configuration configuration;
    private RpcConnectorWrapper connector;

    public CheetahServer (RaftRpcServerAcceptor acceptor) {
        this.acceptor = acceptor;
        configuration = new Configuration();
    }

    /**
     * 配置文件中初始化节点，不提供相互注册
     */
    public void clusterInit() {
        acceptor.startService();
    }

    /**
     * 单个节点启动，需要注册本节点
     */
    public void start() {
        acceptor.startService();
        //register the new node
        RaftConsensusService raftConsensusService = getRaftConsensusService();
        RegisterServerRequest request = new RegisterServerRequest(acceptor.getHost(), acceptor.getPort());
        RegisterServerResponse response = raftConsensusService.registerServer(request);

        //sync info
        acceptor.getRaftCore().setServerList(response.getServerList());
        acceptor.getRaftCore().setServerNodeCache(response.getServerNodeCache());
        acceptor.getRaftNode().setLeaderId(response.getLeaderId());
        acceptor.getRaftNode().setCurrentTerm(response.getCurrentTerm());

        logger.info("cheetah server host=" + acceptor.getHost() + " port=" + acceptor.getPort() +
        " has started!");
    }

    public void stop() {
        acceptor.stopService();
    }

    public RaftConsensusService getRaftConsensusService () {
        CheetahAddress cheetahAddress = null;
        Map<Long, String> initServerList = RaftUtils.getInitCacheServerList(configuration);
        for (Map.Entry<Long, String> entry : initServerList.entrySet()) {
            if (!StringUtils.equals(entry.getValue(), acceptor.getHost() + ":" + acceptor.getPort())) {
                cheetahAddress = ParseUtils.parseAddress(entry.getValue());
                break;
            }
        }

        if (cheetahAddress == null) {
            logger.error("init servers have all down!");
            throw new RuntimeException("can't find server to register!");
        }
        connector = new RpcServerSyncConnector(cheetahAddress.getHost(),
                cheetahAddress.getPort());
        connector.startService();
        SimpleClientRemoteProxy proxy = connector.getProxy();
        return proxy.registerRemote(RaftConsensusService.class);
    }


    public RaftRpcServerAcceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(RaftRpcServerAcceptor acceptor) {
        this.acceptor = acceptor;
    }
}
