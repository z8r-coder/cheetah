package rpc.demo.rpc.nio;

import org.apache.log4j.Logger;
import rpc.client.SimpleClientRemoteExecutor;
import rpc.client.SimpleClientRemoteProxy;
import rpc.demo.rpc.provider.HelloRpcService;
import rpc.net.AbstractRpcConnector;
import rpc.nio.RpcNioConnector;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Roy
 * @Date: Created in 17:03 2017/12/3 0003
 */
public class RpcNioClient {
    private static Logger logger = Logger.getLogger(RpcNioClient.class);

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 4332;

        String host1 = "127.0.0.1";
        int port1 = 4332;

        AbstractRpcConnector connector = new RpcNioConnector(null);

        AbstractRpcConnector connector1 = new RpcNioConnector(null);
        connector.setHost(host);
        connector.setPort(port);

        connector1.setHost(host1);
        connector1.setPort(port1);

        List<AbstractRpcConnector> connectors = new ArrayList<AbstractRpcConnector>();
        connectors.add(connector);connectors.add(connector1);

        SimpleClientRemoteExecutor executor = new SimpleClientRemoteExecutor(connectors);

        SimpleClientRemoteProxy proxy = new SimpleClientRemoteProxy(executor);

        proxy.startService();


//        HelloRpcService htest = new HelloRpcServiceImpl();
//        htest.sayHello("this is test",564);
        HelloRpcService helloRpcService = proxy.registerRemote(HelloRpcService.class);
        logger.info("start client");
//
//        helloRpcService.sayHello("this is HelloRpcService",564);
//
//        helloRpcService.sayHello("this is HelloRpcService tttttttt",3333);
//      -----------------------   test broadcast --------------------------
//        String hello = helloRpcService.getHello();

//        int ex = helloRpcService.callException(false);

//        logger.info("hello result:"+hello);

//        logger.info("exResult:"+ex);

//        long start = System.currentTimeMillis();
//        int total = 10000;
//        for(int i=0;i<total;i++){
//            helloRpcService.sayHello("this is HelloRpcService",564);
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("cost:"+(end-start));

        helloRpcService.addMessage("this A");
        helloRpcService.addMessage("this B");

        helloRpcService.printList();

        helloRpcService.printListSize();

//        proxy.stopService();
    }
}
