package rpc.register;

import rpc.RpcRegisterCenter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc
 */
public class AddrRegisterCenter<T> implements RpcRegisterCenter<T>{

    private Set<String> addrSet = new HashSet<String>();


    public synchronized void register(T address) {
        addrSet.add((String) address);
    }

    public synchronized void unRegister (T address) {
        addrSet.remove(address);
    }

    public Set<String> getAddrSet() {
        return addrSet;
    }
}
