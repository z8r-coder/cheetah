package rpc.registry;

import models.CheetahAddress;

import java.util.List;
import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册信息
 */
public interface IServerRegisterInfo {

    public Set<String> getServerList();

    public void updateList();

    void register (String address);

    void unRegister (String address);

    void heartBeat (String address);
}
