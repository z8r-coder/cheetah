package rpc.nio;

import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.RpcTask;
import rpc.exception.RpcException;
import rpc.net.AbstractRpcConnector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author:Roy
 * @Date: Created in 1:02 2017/10/15 0015
 */
public class SimpleRpcNioSelector extends AbstractRpcNioSelector {
    private Selector selector;
    private boolean stop = false;
    private boolean started = false;
    private ConcurrentHashMap<SocketChannel, RpcNioConnector> connectorCache;
    private List<RpcNioConnector> connectors;
    private ConcurrentHashMap<ServerSocketChannel, RpcNioAcceptor> acceptorCache;
    private List<RpcNioAcceptor> acceptors;
    private final static int READ_OP = SelectionKey.OP_READ;
    private final static int READ_WRITE_OP = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    private LinkedList<RpcTask> selectTasks = new LinkedList<RpcTask>();

    private AbstractRpcNioSelector delegageSelector;

    private Logger logger = Logger.getLogger(SimpleRpcNioSelector.class);

    public SimpleRpcNioSelector() {
        super();
        try {
            selector = Selector.open();
            connectorCache = new ConcurrentHashMap<SocketChannel, RpcNioConnector>();
            connectors = new CopyOnWriteArrayList<RpcNioConnector>();
            acceptorCache = new ConcurrentHashMap<ServerSocketChannel, RpcNioAcceptor>();
            acceptors = new CopyOnWriteArrayList<RpcNioAcceptor>();
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private void initNewSocketChannel(SocketChannel channel, RpcNioConnector connector,
                                      SelectionKey selectionKey) {
        if (connector.getAcceptor() != null) {
            connector.getAcceptor().addConnectorListener(connector);
        }
        connector.setSelectionKey(selectionKey);
        connectorCache.put(channel, connector);
        connectors.add(connector);
    }

    public void register(final RpcNioAcceptor acceptor) {
        final ServerSocketChannel channel = acceptor.getServerSocketChannel();
            this.addSelectTask(new RpcTask() {
                public void run() {
                    try {
                        channel.register(selector, SelectionKey.OP_ACCEPT);
                    } catch (Exception e) {
                        acceptor.handleNetException(e);
                    }
                }
            });
            this.notifySend(null);
            acceptorCache.put(acceptor.getServerSocketChannel(), acceptor);
            acceptors.add(acceptor);
    }

    public void unRegister(RpcNioAcceptor acceptor) {
        ServerSocketChannel channel = acceptor.getServerSocketChannel();
        acceptorCache.remove(channel);
        acceptors.remove(acceptor);
    }

    public void register(final RpcNioConnector connector) {
        this.addSelectTask(new RpcTask() {
            public void run() {
                try {
                    SelectionKey selectionKey = connector.getChannel().register(selector, READ_OP);
                    SimpleRpcNioSelector.this.initNewSocketChannel(connector.getChannel(), connector, selectionKey);
                } catch (ClosedChannelException e) {
                    connector.handleNetException(e);
                }
            }
        });
        this.notifySend(null);
    }

    public void unRegister(RpcNioConnector connector) {
        connectorCache.remove(connector.getChannel());
        connectors.remove(connector);
    }

    public synchronized void startService() {
        if (!started) {
            //select ready channels to op
            new SelectionThread().start();
            started = true;
        }
    }

    public void stopService() {
        this.stop = true;
    }

    public void handleNetException(Exception e) {
        logger.error("selector exception:" + e.getMessage());
    }

    public void notifySend(AbstractRpcConnector connector) {
        selector.wakeup();
    }

    private boolean checkSend() {
        boolean needSend = false;
        for (RpcNioConnector connector : connectors) {
            if (connector.isNeedToSend()) {
                SelectionKey selectionKey = connector.getChannel().keyFor(selector);
                selectionKey.interestOps(READ_WRITE_OP);
                needSend = true;
            }
        }
        return needSend;
    }

    private void addSelectTask(RpcTask task) {
        selectTasks.offer(task);
    }

    private boolean hasTask() {
        RpcTask peek = selectTasks.peek();
        return peek != null;
    }

    private void runSelectTasks() {
        RpcTask peek = selectTasks.peek();
        while (peek != null) {
            peek = selectTasks.poll();
            peek.run();
            peek = selectTasks.peek();
        }
    }

    public void setDelegageSelector(AbstractRpcNioSelector delegageSelector) {
        this.delegageSelector = delegageSelector;
    }

    private class SelectionThread extends Thread {
        @Override
        public void run() {
            logger.info("select thread has started:" + Thread.currentThread().getId());
            while (!stop) {
                if (SimpleRpcNioSelector.this.hasTask()) {
                    SimpleRpcNioSelector.this.runSelectTasks();
                }
                boolean needSend = checkSend();
                try {
                    if (needSend) {
                        selector.selectNow();
                    } else {
                        selector.select();
                    }
                } catch (IOException e) {
                    SimpleRpcNioSelector.this.handleNetException(e);
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    doDispatchSelectionKey(selectionKey);
                }
            }
        }
    }

    private boolean doDispatchSelectionKey(SelectionKey selectionKey) {
        boolean res = false;
        try {
            if (selectionKey.isAcceptable()) {
                res = doAccept(selectionKey);
            }
            if (selectionKey.isReadable()) {
                res = doRead(selectionKey);
            }
            if (selectionKey.isWritable()) {
                res = doWrite(selectionKey);
            }
        } catch (Exception e) {
            this.handSelectionKeyException(selectionKey, e);
        }
        return res;
    }

    private boolean doAccept(SelectionKey selectionKey) {
        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
        RpcNioAcceptor acceptor = acceptorCache.get(server);
        try {
            SocketChannel client = server.accept();
            if (client != null) {
                if (client.socket() != null) {
                    logger.warn( "----" + client.socket().getRemoteSocketAddress() + "-----");
                }
                client.configureBlocking(false);
                if (delegageSelector != null) {
                    RpcNioConnector connector = new RpcNioConnector(client, delegageSelector);
                    connector.setAcceptor(acceptor);
                    connector.setExecutorService(acceptor.getExecutorService());
                    connector.setExecutorSharable(true);
                    delegageSelector.register(connector);
                    connector.startService();
                } else {
                    RpcNioConnector connector = new RpcNioConnector(client, this);
                    connector.setAcceptor(acceptor);
                    connector.setExecutorService(acceptor.getExecutorService());
                    connector.setExecutorSharable(true);
                    this.register(connector);
                    connector.startService();
                }
                return true;
            }
        } catch (IOException e) {
            this.handSelectionKeyException(selectionKey, e);
        }
        return false;
    }

    private void fireRpc(RpcNioConnector connector, RpcObject rpc) {
        rpc.setHost(connector.getRemoteHost());
        rpc.setPort(connector.getRemotePort());
        rpc.setRpcContext(connector.getRpcContext());
        connector.fireCall(rpc);
    }

    private boolean doRead(SelectionKey selectionKey) {
        boolean result = false;
        SocketChannel client = (SocketChannel) selectionKey.channel();
        RpcNioConnector connector = connectorCache.get(client);
        if (connector != null) {
            try {
                RpcNioBuffer connectorReadBuf = connector.getRpcNioReadBuffer();
                ByteBuffer channelReadBuf = connector.getChannelReadBuffer();
                while (!stop) {
                    int read = 0;
                    while ((read = client.read(channelReadBuf)) > 0) {
                        channelReadBuf.flip();
                        byte[] readBytes = new byte[read];
                        channelReadBuf.get(readBytes);
                        connectorReadBuf.write(readBytes);
                        channelReadBuf.clear();
                        while (connectorReadBuf.hasRpcObject()) {
                            RpcObject rpc = connectorReadBuf.readRpcObject();
                            this.fireRpc(connector, rpc);
                        }
                    }
                    if (read < 1) {
                        if (read < 0) {
                            this.handSelectionKeyException(selectionKey, new RpcException());
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                this.handSelectionKeyException(selectionKey, e);
            }
        }
        return result;
    }

    private boolean doWrite(SelectionKey selectionKey) {
        boolean result = false;
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        RpcNioConnector connector = connectorCache.get(channel);
        if (connector.isNeedToSend()) {
            try {
                RpcNioBuffer connectorWriteBuf = connector.getRpcNioWriteBuffer();
                ByteBuffer channelWriteBuf = connector.getChannelWriteBuffer();
                while (connector.isNeedToSend()) {
                    RpcObject rpc = connector.getToSend();
                    connectorWriteBuf.writeRpcObject(rpc);
                    channelWriteBuf.put(connectorWriteBuf.readBytes());
                    channelWriteBuf.flip();
                    int wantWrite = channelWriteBuf.limit() - channelWriteBuf.position();
                    int write = 0;
                    while (write < wantWrite) {
                        write += channel.write(channelWriteBuf);
                    }
                    channelWriteBuf.clear();
                    result = true;
                }
                if (!connector.isNeedToSend()) {
                    selectionKey.interestOps(READ_OP);
                }
            } catch (IOException e) {
                this.handSelectionKeyException(selectionKey, e);
            }
        }
        return result;
    }

    private void handSelectionKeyException(final SelectionKey selectionKey, Exception e) {
        SelectableChannel channel = selectionKey.channel();
        if (channel instanceof ServerSocketChannel) {
            RpcNioAcceptor acceptor = acceptorCache.get(channel);
            if (acceptor != null) {
                logger.error("acceptor: " + acceptor.getHost() + ":" + acceptor.getPort() +
                " selection error" + e.getClass() + " " + e.getMessage() + " start to shutdown");
                acceptor.stopService();
            }
        } else {
            RpcNioConnector connector = connectorCache.get(channel);
            if (connector != null) {
                logger.error("connector " + connector.getHost() + ":" + connector.getPort() + " selection error"
                        + e.getClass() + " " + e.getMessage() + " start to shutdown");
                this.fireNetListeners(connector, e);
                connector.stopService();
            }
        }
        this.logState();
    }

    private void logState() {
        int acceptorLen = acceptors.size();
        int connectorLen = connectors.size();
        logger.info("acceptors:" + acceptorLen + "connectors:" + connectorLen);
    }
}
