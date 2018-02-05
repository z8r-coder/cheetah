package rpc.demo.nio;


import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.RpcSender;
import rpc.constants.RpcType;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcCallListener;
import rpc.nio.RpcNioConnector;
import rpc.nio.SimpleRpcNioSelector;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:Roy
 * @Date: Created in 15:57 2017/11/11 0011
 */
public class NioClient implements RpcCallListener {
    public static Logger logger = Logger.getLogger(NioClient.class);
    private SimpleRpcNioSelector selection;
    private RpcNioConnector connector;
    private String host = "127.0.0.1";
    private int port = 4332;
    private int threadCount;
    private AtomicInteger send = new AtomicInteger(0);
    private AtomicInteger receive = new AtomicInteger(0);
    private List<Thread> threads;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicInteger cccc = new AtomicInteger(0);

    public NioClient(SimpleRpcNioSelector selection) {
        this.selection = selection;
    }
    public NioClient clone() {
        NioClient client = new NioClient(selection);
        client.host = host;
        client.port = port;
        client.threadCount = threadCount;
        return client;
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleRpcNioSelector selector = new SimpleRpcNioSelector();
        String ip = "127.0.0.1";
        int basePort = 3333;
        int clientCount = 5;
        int connectors = 2;
        int threadCount = 2;
        List<NioClient> clients = createClients(selector, ip, basePort, clientCount, connectors, threadCount);
        startService(clients);
        Thread.currentThread().sleep(60000);
        stopService(clients);
        Thread.currentThread().sleep(10000);
        printResult(clients);
    }

    public static List<NioClient> createClients (SimpleRpcNioSelector selection, String ip, int port, int clients,int connectors, int threadCount) {
        List<NioClient> list = new LinkedList<NioClient>();
        int i = 0;
        while (i < clients) {
            NioClient client = new NioClient(selection);
            client.host = ip;
            client.port = port + i;
            client.threadCount = threadCount;

            list.add(client);
            int con = 0;
            while (con < connectors) {
                list.add(client.clone());
                con++;
            }
            i++;
        }
        return list;
    }
    public static void printResult(List<NioClient> clients) {
        for (NioClient client : clients) {
            client.printResult();
        }
    }

    public static void startService(List<NioClient> clients) {
        int i = 0;
        for (NioClient client : clients) {
            client.startService();
            i++;
        }
        logger.info("start client count:" + i);
    }
    public void onRpcMessage(RpcObject rpc, RpcSender sender) {
        receive.incrementAndGet();
    }

    public class SendThread extends Thread {
        private AbstractRpcConnector connector;
        private int interval;
        private int index;

        public SendThread(AbstractRpcConnector connector, int interval, int startIndex) {
            this.connector = connector;
            this.interval = interval;
            this.index = startIndex;
        }

        @Override
        public void run() {
            String prefix = "rpc test index";
            long threadId = Thread.currentThread().getId();
            logger.info("send thread:"+ threadId + " start" + host + ":" + port);
            while (true) {
                RpcObject rpc = createRpc(prefix + index, threadId, index);
                connector.sendRpcObject(rpc, 10000);
                NioClient.this.send.incrementAndGet();
                index++;
                try {
                    Thread.currentThread().sleep(interval);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    public static void stopService(List<NioClient> clients) {
        for (NioClient client : clients) {
            client.stopService();
        }
    }
    public static RpcObject createRpc(String str, long id, int index) {
        RpcObject rpc = new RpcObject();
        rpc.setType(RpcType.INVOKE);
        rpc.setIndex(index);
        rpc.setThreadId(id);
        rpc.setData(str.getBytes());
        rpc.setLength(rpc.getData().length);
        return rpc;
    }

    public void startService() {
        if (!started.get()) {
            started.set(true);
            connector = new RpcNioConnector(selection);
            connector.setHost(host);
            connector.setPort(port);
            connector.addRpcCallListener(this);
            connector.startService();
            threads = startThread(connector, threadCount);
            cccc.incrementAndGet();
            logger.info("start time :" + cccc.get());
        }
    }

    private List<Thread> startThread(AbstractRpcConnector connector, int count) {
        LinkedList<Thread> linkedList = new LinkedList<Thread>();
        int c = 0;
        Random random = new Random();
        while (c < count) {
            int intervel = random.nextInt(200);
            int index = random.nextInt(20000);
            SendThread thread = new SendThread(connector, intervel, index);
            linkedList.add(thread);
            thread.start();
            c++;
        }
        return linkedList;
    }

    public void printResult(){
        logger.info(this.host+":"+this.port+"  send:"+send.get()+" receive:"+receive.get());
    }

    public void stopService() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
