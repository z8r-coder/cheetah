package rpc.monitor;

import rpc.RpcService;
import rpc.RpcServiceBean;
import rpc.server.RpcServicesHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class LocalMonitorService implements RpcMonitorService {

    private RpcServicesHolder rpcServicesHolder;
    private long time = 0;

    public LocalMonitorService (RpcServicesHolder rpcServicesHolder) {
        this.rpcServicesHolder = rpcServicesHolder;
    }

    public List<RpcService> getRpcServices() {
        if (rpcServicesHolder != null) {
            List<RpcServiceBean> serviceBeans = rpcServicesHolder.getRpcServices();
            if (serviceBeans != null && serviceBeans.size() > 0) {
                List<RpcService> list = new ArrayList<RpcService>();
                for (RpcServiceBean service : serviceBeans) {
                    RpcService rpcService = new RpcService(service.getInterf().getName(),service.getVersion(),service.getBean().getClass().getName());
                    rpcService.setTime(time);
                    list.add(rpcService);
                }
                return list;
            }
        }
        return Collections.emptyList();
    }

    public String ping() {
        return "pong " + new Date();
    }
}
