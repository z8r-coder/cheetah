package rpc.registry;

import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册信息
 */
public interface IServerRegisterInfo {

    public Set<String> getServerList();

    void register (String obj);

    void unRegister (String obj);
}
