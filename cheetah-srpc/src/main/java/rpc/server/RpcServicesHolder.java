package rpc.server;

import rpc.RpcServiceBean;

import java.util.List;

/**
 * provider提供api列表
 * @Author:Roy
 * @Date: Created in 17:57 2017/12/3 0003
 */
public interface RpcServicesHolder {

    public List<RpcServiceBean> getRpcServices();
}
