package rpc.registry;

/**
 * @author ruanxin
 * @create 2018-02-09
 * @desc
 */
public class SimpleRegisterProxy {

    private SimpleRegisterServer registerServer;

    public SimpleRegisterProxy (SimpleRegisterServer registerServer) {
        this.registerServer = registerServer;
    }

    public void execute() {

    }
}
