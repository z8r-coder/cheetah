package rpc.registry;

import models.CheetahAddress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    Set<CheetahAddress> serverList = new CopyOnWriteArraySet<CheetahAddress>();

    Set<CheetahAddress> heartBeatList = new CopyOnWriteArraySet<CheetahAddress>();

    public Set<CheetahAddress> getServerList() {
        return serverList;
    }

    public void updateList() {
        serverList = heartBeatList;
        heartBeatList.clear();
    }

    public void register(CheetahAddress address) {
        serverList.add(address);
    }

    public void unRegister(CheetahAddress address) {
        serverList.remove(address);
    }

    public void heartBeat(CheetahAddress address) {
        heartBeatList.add(address);
    }
}
