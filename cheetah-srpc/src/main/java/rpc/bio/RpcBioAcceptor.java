package rpc.bio;


import org.apache.log4j.Logger;
import rpc.exception.RpcException;
import rpc.net.AbstractRpcAcceptor;
import rpc.net.AbstractRpcWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by rx on 2017/12/4.
 */
public class RpcBioAcceptor extends AbstractRpcAcceptor {

    private final static Logger logger = Logger.getLogger(RpcBioAcceptor.class);
    private ServerSocket serverSocket;
    private AbstractRpcWriter writer;

    public RpcBioAcceptor () {
        this(null);
    }

    public RpcBioAcceptor (AbstractRpcWriter rpcWriter) {
        try {
            serverSocket = new ServerSocket();
            if (rpcWriter == null) {
                writer = new RpcBioWriter();
            } else {
                this.writer = rpcWriter;
            }
        } catch (IOException e) {
            this.handleNetException(e);
        }
    }
    @Override
    public void startService() {
        super.startService();
        try {
            serverSocket.bind(new InetSocketAddress(this.getHost(), this.getPort()));
            new BioAcceptorThread().start();
            this.startListeners();
            this.fireStartNetListeners();
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void stopService() {
        super.stopService();
        stop = true;
        this.stopListeners();
        try {
            serverSocket.close();
        } catch (IOException e) {
            //ignore
        }
    }

    private class BioAcceptorThread extends Thread {
        @Override
        public void run() {
            while (!stop) {
                try {
                    Socket socket = serverSocket.accept();
                    RpcBioConnector connector = new RpcBioConnector(writer, socket);
                    connector.setExecutorService(RpcBioAcceptor.this.getExecutorService());
                    connector.setExecutorSharable(true);
                    RpcBioAcceptor.this.addConnectorListener(connector);
                    connector.startService();
                } catch (IOException e) {
                    RpcBioAcceptor.this.handleNetException(e);
                }
            }

        }
    }


    public void handleNetException(Exception e) {
        logger.error("bio acceptor io exception, start to shutdown service!");
        this.stopService();
        throw new RpcException(e);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public AbstractRpcWriter getWriter() {
        return writer;
    }

    public void setWriter(AbstractRpcWriter writer) {
        this.writer = writer;
    }
}
