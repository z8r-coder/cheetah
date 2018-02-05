package rpc.bio;


import org.apache.log4j.Logger;
import rpc.RpcObject;
import rpc.net.AbstractRpcConnector;
import rpc.net.AbstractRpcWriter;
import rpc.utils.RpcUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by rx on 2017/12/4.
 */
public class RpcBioConnector extends AbstractRpcConnector {

    private final static Logger log = Logger.getLogger(RpcBioConnector.class);

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public RpcBioConnector(AbstractRpcWriter rpcWriter) {
        super(rpcWriter);

    }

    public RpcBioConnector(AbstractRpcWriter rpcWriter, Socket socket) {
        super(rpcWriter);
        this.socket = socket;
    }

    @Override
    public void startService() {
        super.startService();

        try {
            if (socket == null) {
                //client call
                socket = new Socket();
                socket.connect(new InetSocketAddress(this.getHost(), this.getPort()));
                log.info("connect to " + getHost() + ":" + getPort() + "success!");
            }
            InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            remoteHost = remoteAddress.getAddress().getHostAddress();
            remotePort = remoteAddress.getPort();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            getWriter().registerWrite(this);
            getWriter().startService();
            new ClientThreand().start();
            this.fireStartNetListeners();
        } catch (IOException e) {
            this.handleNetException(e);
        }
    }

    private class ClientThreand extends Thread {
        @Override
        public void run() {
            while (!stop) {
                RpcObject rpcObject = RpcUtils.readDataRpc(dis, RpcBioConnector.this);
                rpcObject.setHost(remoteHost);
                rpcObject.setPort(remotePort);
                rpcObject.setRpcContext(rpcContext);
                fireCall(rpcObject);
            }
        }
    }

    @Override
    public void stopService() {
        super.stopService();
        stop = true;
        RpcUtils.close(dis, dos);
        try {
            socket.close();
        } catch (IOException e) {
            //ignore
        }
        rpcContext.clear();
        sendQueueCache.clear();
    }

    public void handleNetException(Exception e) {
        log.info("RpcBioConnector occurs error, stop the service!");
        this.getWriter().unRegisterWrite(this);
        this.stopService();

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }
}
