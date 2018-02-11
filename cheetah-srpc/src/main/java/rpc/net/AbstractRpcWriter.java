package rpc.net;

import org.apache.log4j.Logger;
import rpc.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author:Roy
 * @Date: Created in 23:15 2017/10/14 0014
 */
public abstract class AbstractRpcWriter implements Service, RpcOutputNofity {

    private Logger logger = Logger.getLogger(AbstractRpcWriter.class);
    //every connector need to reg, and get a socket to write by connector
    private List<AbstractRpcConnector> connectors;
    protected Thread sendThread;
    private int interval = 50;
    private boolean stop = false;
    private AtomicBoolean started = new AtomicBoolean(false);

    public AbstractRpcWriter() {
        connectors = new CopyOnWriteArrayList<AbstractRpcConnector>();
    }
    public void registerWrite(AbstractRpcConnector connector) {
        connectors.add(connector);
    }

    public void unRegisterWrite(AbstractRpcConnector connector) {
        connectors.remove(connector);
    }

    public void startService() {
        if (!started.get()) {
            sendThread = new WriteThread();
            sendThread.start();
            started.set(true);
        }
    }

    public void stopService() {
        stop = true;
        sendThread.interrupt();
    }

    public void notifySend(AbstractRpcConnector connector) {
        sendThread.interrupt();
    }

    public abstract boolean doSend(AbstractRpcConnector connector);

    private class WriteThread extends Thread {
        @Override
        public void run() {
            boolean hasSend = false;
            while (!stop) {
                try {
                    for (AbstractRpcConnector connector:connectors) {
                        hasSend |= doSend(connector);
                    }
                    if (!hasSend) {
                        Thread.currentThread().sleep(interval);
                    }
                    hasSend = false;
                }catch (InterruptedException e) {
                    //notify to send
                }
            }
        }
    }
}
