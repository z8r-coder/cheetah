package rpc.monitor;

import rpc.RpcService;
import rpc.RpcServiceBean;
import rpc.server.RpcServicesHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class LocalMonitorService implements RpcMonitorService {

    private RpcServicesHolder rpcServicesHolder;

    public LocalMonitorService (RpcServicesHolder rpcServicesHolder) {
        this.rpcServicesHolder = rpcServicesHolder;
    }

    public List<RpcService> getRpcServices() {
        if (rpcServicesHolder != null) {
            List<RpcServiceBean> serviceBeans = rpcServicesHolder.getRpcServices();
            if (serviceBeans != null && serviceBeans.size() > 0) {
                List<RpcService> list = new ArrayList<RpcService>();
                for (RpcServiceBean serviceBean : serviceBeans) {
//                    RpcService rpcService = new RpcSer
                }
            }
        }
        return null;
    }

    public String ping() {
        return null;
    }
}
