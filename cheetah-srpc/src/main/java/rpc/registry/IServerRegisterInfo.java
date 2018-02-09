package rpc.registry;

/**
 * @author ruanxin
 * @create 2018-02-08
 * @desc 注册中心 SPI
 */
public interface IServerRegisterInfo {

    void register (String obj);

    void unRegister (String obj);
}
