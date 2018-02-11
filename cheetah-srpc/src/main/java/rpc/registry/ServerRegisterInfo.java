package rpc.registry;

import models.CheetahAddress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    List<CheetahAddress> serverList = new CopyOnWriteArrayList<CheetahAddress>();

    public List<CheetahAddress> getServerList() {
        return serverList;
    }

    public synchronized void register(CheetahAddress address) {
        serverList.add(address);
    }

    public synchronized void unRegister(CheetahAddress address) {
        serverList.remove(address);
    }
}
