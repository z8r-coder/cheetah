package rpc.demo.nio;


import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.RpcSender;
import rpc.net.RpcCallListener;
import rpc.nio.RpcNioAcceptor;
import rpc.nio.RpcNioConnector;
import rpc.nio.SimpleRpcNioSelector;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:Roy
 * @Date: Created in 16:51 2017/12/2 0002
 */
public class NioServer implements RpcCallListener {

    private static Logger logger = Logger.getLogger(NioServer.class);
    RpcNioAcceptor acceptor;
    private SimpleRpcNioSelector selection;
    private String host;
    private int port;
    private AtomicInteger receive = new AtomicInteger(0);
    private AtomicBoolean started = new AtomicBoolean(false);
    private ConcurrentHashMap<String, AtomicInteger>  count = new ConcurrentHashMap<String, AtomicInteger>();

    public NioServer(SimpleRpcNioSelector selection) {
        this.selection = selection;
    }

    public void startService() {
        if (!started.get()) {
            acceptor = new RpcNioAcceptor(selection);
            acceptor.setHost(host);
            acceptor.setPort(port);
            acceptor.addRpcCallListener(this);
            acceptor.startService();
            started.set(true);
        }
    }

    public void printResult() {
        String hostName = host + ":" + port;
        logger.info(hostName + "receive count all:" + receive.get());
        Enumeration<String> keys = count.keys();
        int i=1;
        while(keys.hasMoreElements()){
            String sender = keys.nextElement();
            AtomicInteger c = count.get(sender);
            logger.info("host:" + hostName + " client " + sender + " count:" + c.get());
            i++;
        }
    }

    public static List<NioServer> createServers(SimpleRpcNioSelector selection, int c, String ip, int basePort) {
        if (selection == null) {
            selection = new SimpleRpcNioSelector();
        }
        List<NioServer> servers = new LinkedList<NioServer>();
        int i = 0;
        while (i < c) {
            NioServer server = new NioServer(selection);
            server.host = ip;
            server.port = basePort + i;
            i++;
            servers.add(server);
        }
        return servers;
    }

    public static void startService(List<NioServer> servers) {
        for (NioServer server:servers) {
            server.startService();
        }
    }

    public static void printResult(List<NioServer> servers){
        for(NioServer server:servers){
            server.printResult();
        }
    }

    public void stopService() {
        acceptor.stopService();
    }
    public void onRpcMessage(RpcObject rpc, RpcSender sender) {
        sender.sendRpcObject(rpc, 1000);
        RpcNioConnector connector = (RpcNioConnector) sender;
        String clientKey = connector.getRemoteHost() + ":" + connector.getRemotePort();
        AtomicInteger c = count.get(clientKey);
        if (c == null) {
            c = new AtomicInteger(1);
            count.put(clientKey, c);
        } else {
            c.incrementAndGet();
        }
        receive.incrementAndGet();
    }

    public static void main(String args[]) {
        SimpleRpcNioSelector selection = new SimpleRpcNioSelector();
        String ip = "127.0.0.1";
        int port = 3333;
        int c = 5;
        List<NioServer> servers = createServers(selection,c,ip,port);
        startService(servers);
        try {
            Thread.currentThread().sleep(80000);
        } catch (InterruptedException e) {

        }
        printResult(servers);
    }
}
