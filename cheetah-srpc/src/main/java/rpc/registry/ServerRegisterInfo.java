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

    Set<String> serverList = new CopyOnWriteArraySet<String>();

    Set<String> heartBeatList = new CopyOnWriteArraySet<String>();

    public Set<String> getServerList() {
        return serverList;
    }

    public void updateList() {
        serverList.clear();
        serverList.addAll(heartBeatList);
        heartBeatList.clear();
    }

    public void register(String address) {
        serverList.add(address);
        heartBeatList.add(address);
    }

    public void unRegister(String address) {
        serverList.remove(address);
    }

    public void heartBeat(String  address) {
        heartBeatList.add(address);
    }
}
