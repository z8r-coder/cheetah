package rpc.registry;

import models.CheetahAddress;
import utils.ParseUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    Map<Long, String> serverCache = new ConcurrentHashMap<>();

    Map<Long, String> heartBeatCache = new ConcurrentHashMap<>();

    public void updateList() {
        serverCache.clear();
        serverCache.putAll(heartBeatCache);
        heartBeatCache.clear();
    }

    public Map<Long, String> register(String address) {
        long serverId = getServerId(address);
        serverCache.put(serverId,address);
        heartBeatCache.put(serverId, address);
        return getServerListCache();
    }

    public void unRegister(String address) {
        heartBeatCache.remove(address);
        serverCache.remove(address);
        long serverId = getServerId(address);
        serverCache.remove(serverId);
    }

    public Map<Long, String> heartBeat(String address) {
        long serverId = getServerId(address);
        heartBeatCache.put(serverId, address);
        return getServerListCache();
    }

    public Map<Long, String> getServerListCache() {
        Map<Long, String> cloneCache = new HashMap<>();
        synchronized (serverCache) {
            cloneCache.putAll(serverCache);
        }
        return cloneCache;
    }

    private long getServerId(String address) {
        CheetahAddress cheetahAddress = ParseUtils.parseAddress(address);
        long serverId = ParseUtils.generateServerId(cheetahAddress.getHost(), cheetahAddress.getPort());
        return serverId;
    }
}
