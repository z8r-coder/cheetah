package rpc.nio;

import org.apache.log4j.Logger;
import rpc.exception.RpcException;
import rpc.net.AbstractRpcConnector;
import rpc.net.RpcNetListener;
import rpc.utils.RpcUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @Author:Roy
 * @Date: Created in 22:48 2017/10/14 0014
 */
public class RpcNioConnector extends AbstractRpcConnector {

    private SocketChannel channel;
    private AbstractRpcNioSelector selector;
    private ByteBuffer channelWriteBuffer;
    private ByteBuffer channelReadBuffer;
    private SelectionKey selectionKey;

    private RpcNioBuffer rpcNioReadBuffer;
    private RpcNioBuffer rpcNioWriteBuffer;

    private RpcNioAcceptor acceptor;

    private static final Logger logger = Logger.getLogger(RpcNioConnector.class);

    public RpcNioConnector(SocketChannel socketChannel, AbstractRpcNioSelector selection) {
        this(selection);
        this.channel = socketChannel;
    }
    public RpcNioConnector(AbstractRpcNioSelector selector) {
        super(null);
        if (selector == null) {
            this.selector = new SimpleRpcNioSelector();
        } else {
            this.selector = selector;
        }
        this.initBuf();
    }


    public void startService() {
        super.startService();
        try {
            if (channel == null) {
                channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(this.getHost(), this.getPort()));
                channel.configureBlocking(false);
                while (!channel.isConnected());
                logger.info("connect to "+this.getHost()+":"+this.getPort()+" success");
                selector.startService();
                selector.register(this);
            }
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
            InetSocketAddress localAddress = (InetSocketAddress) channel.socket().getLocalSocketAddress();
            String remote = RpcUtils.genAddressString("remoteAddress->", remoteAddress);
            String local = RpcUtils.genAddressString("localAddress->", localAddress);
            logger.info(local + " " + remote);
            remotePort = remoteAddress.getPort();
            remoteHost = remoteAddress.getAddress().getHostAddress();
            this.fireStartNetListeners();
        } catch(IOException e){
            logger.error("connect to host "+this.getHost()+" port "+this.getPort()+" failed", e);
            throw new RpcException("connect to host error");
        }
    }

    public void stopService() {
        super.stopService();
        this.selector.unRegister(this);
        this.sendQueueCache.clear();
        this.rpcContext.clear();
        try {
            channel.close();
            channelWriteBuffer.clear();
            channelReadBuffer.clear();
            rpcNioReadBuffer.clear();
            rpcNioWriteBuffer.clear();
        } catch (IOException e) {
            //ignore
        }
        this.stop = true;
    }

    public boolean isValid() {
        return !stop;
    }

    public void handleNetException(Exception e) {
        logger.error("connector " + this.getHost() + ":" + this.getPort() + " io exception start to shutdown");
        this.stopService();
    }

    @Override
    public void addRpcNetListener(RpcNetListener listener) {
        super.addRpcNetListener(listener);
        this.selector.addRpcNetListener(listener);
    }

    @Override
    public void notifySend() {
        selector.notifySend(this);
    }

    private void initBuf(){
        channelWriteBuffer = ByteBuffer.allocate(RpcUtils.MEM_512KB);
        channelReadBuffer = ByteBuffer.allocate(RpcUtils.MEM_512KB);
        rpcNioReadBuffer = new RpcNioBuffer(RpcUtils.MEM_512KB);
        rpcNioWriteBuffer = new RpcNioBuffer(RpcUtils.MEM_512KB);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public AbstractRpcNioSelector getSelector() {
        return selector;
    }

    public void setSelector(AbstractRpcNioSelector selector) {
        this.selector = selector;
    }

    public ByteBuffer getChannelWriteBuffer() {
        return channelWriteBuffer;
    }

    public void setChannelWriteBuffer(ByteBuffer channelWriteBuffer) {
        this.channelWriteBuffer = channelWriteBuffer;
    }

    public ByteBuffer getChannelReadBuffer() {
        return channelReadBuffer;
    }

    public void setChannelReadBuffer(ByteBuffer channelReadBuffer) {
        this.channelReadBuffer = channelReadBuffer;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public RpcNioBuffer getRpcNioReadBuffer() {
        return rpcNioReadBuffer;
    }

    public void setRpcNioReadBuffer(RpcNioBuffer rpcNioReadBuffer) {
        this.rpcNioReadBuffer = rpcNioReadBuffer;
    }

    public RpcNioBuffer getRpcNioWriteBuffer() {
        return rpcNioWriteBuffer;
    }

    public void setRpcNioWriteBuffer(RpcNioBuffer rpcNioWriteBuffer) {
        this.rpcNioWriteBuffer = rpcNioWriteBuffer;
    }

    public RpcNioAcceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(RpcNioAcceptor acceptor) {
        this.acceptor = acceptor;
    }
}
