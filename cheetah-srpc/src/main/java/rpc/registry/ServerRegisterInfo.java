package rpc.registry;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class ServerRegisterInfo implements IServerRegisterInfo {

    Set<String> serverList = new HashSet<String>();

    public Set<String> getServerList() {
        return serverList;
    }

    public synchronized void register(String address) {
        serverList.add(address);
    }

    public synchronized void unRegister(String address) {
        serverList.remove(address);
    }
}
