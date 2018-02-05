package rpc.bio;


import rpc.RpcObject;
import rpc.net.AbstractRpcConnector;
import rpc.net.AbstractRpcWriter;
import rpc.utils.RpcUtils;

import java.io.DataOutputStream;

/**
 * Created by rx on 2017/12/4.
 */
public class RpcBioWriter extends AbstractRpcWriter {
    public RpcBioWriter() {
        super();
    }

    public boolean doSend(AbstractRpcConnector connector) {
        boolean needSend = false;
        RpcBioConnector con = (RpcBioConnector) connector;
        DataOutputStream dos = con.getDos();
        while (con.isNeedToSend()) {
            RpcObject rpcObject = con.getToSend();
            RpcUtils.writeDataRpc(rpcObject, dos, con);
            needSend = true;
        }
        return needSend;
    }
}
