package rpc.registry;

import models.HeartBeatRequest;
import models.HeartBeatResponse;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class RegisterHeartBeat implements IRegisterHeartBeat {

    private HeartBeatResponse response;

    public RegisterHeartBeat (HeartBeatResponse response) {
        this.response = response;
    }

    public HeartBeatResponse registerHeartBeat(HeartBeatRequest request) {
        return response;
    }
}
