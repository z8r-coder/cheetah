package rpc.registry;

import java.util.Map;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册信息
 */
public interface IServerRegisterInfo {

    void updateList();

    Map<Integer, String> register (String address);

    void unRegister (String address);

    Map<Integer, String> heartBeat (String address);

    Map<Integer, String> getServerListCache();
}
