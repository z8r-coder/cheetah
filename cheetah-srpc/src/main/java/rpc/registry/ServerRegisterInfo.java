package rpc.registry;

import models.CheetahAddress;
import utils.ParseUtils;

import java.util.*;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    Map<Integer, String> serverCache = new HashMap<Integer, String>();

    Map<Integer, String> heartBeatCache = new HashMap<Integer, String>();

    public void updateList() {
        serverCache.clear();
        serverCache.putAll(heartBeatCache);
        heartBeatCache.clear();
    }

    public void register(String address) {
        int serverId = getServerId(address);
        serverCache.put(serverId,address);
        heartBeatCache.put(serverId, address);
    }

    public void unRegister(String address) {
        heartBeatCache.remove(address);
        serverCache.remove(address);
        int serverId = getServerId(address);
        serverCache.remove(serverId);
    }

    public void heartBeat(String address) {
        int serverId = getServerId(address);
        heartBeatCache.put(serverId, address);
    }

    public Map<Integer, String> getServerListCache() {
        return serverCache;
    }

    private int getServerId(String address) {
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
        int serverId = ParseUtils.generateServerId(cheetahAddress.getHost(), cheetahAddress.getPort());
        return serverId;
    }
}
