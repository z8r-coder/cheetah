package rpc.registry;

import models.CheetahAddress;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    Set<CheetahAddress> serverList = new HashSet<CheetahAddress>();

    public Set<CheetahAddress> getServerList() {
        return serverList;
    }

    public synchronized void register(CheetahAddress address) {
        serverList.add(address);
    }

    public synchronized void unRegister(CheetahAddress address) {
        serverList.remove(address);
    }
}
