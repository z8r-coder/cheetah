package rpc.monitor;

import rpc.RpcService;

import java.util.List;

/**
 * monitor spi for rpc
 * @author royruan
 * @date 2018-1-17
 */
public interface RpcMonitorService {
    /**
     * get local service list
     */
    List<RpcService> getRpcServices();

    /**
     * check
     * @return
     */
    String ping();
}
