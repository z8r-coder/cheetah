package rpc.registry;

import models.CheetahAddress;

import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册信息
 */
public interface IServerRegisterInfo {

    public Set<CheetahAddress> getServerList();

    void register (CheetahAddress address);

    void unRegister (CheetahAddress address);
}
