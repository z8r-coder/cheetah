package rpc;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册中心 SPI
 */
public interface RpcRegisterCenter<T> {

    public void register (T obj);

    public void unRegister(T obj);
}
