package raft.demo.callback;

import raft.protocol.response.VotedResponse;
import rpc.async.RpcCallback;

/**
 * @author ruanxin
 * @create 2018-04-18
 * @desc
 */
public class TestCallBack implements RpcCallback<VotedResponse> {
    @Override
    public void success(VotedResponse resp) {
        System.out.println(resp.getServerId());
    }

    @Override
    public void fail(Throwable t) {

    }
}
