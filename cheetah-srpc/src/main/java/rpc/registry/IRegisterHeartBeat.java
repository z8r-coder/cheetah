package rpc.registry;

import models.HeartBeatRequest;
import models.HeartBeatResponse;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public interface IRegisterHeartBeat {

    public HeartBeatResponse registerHeartBeat(HeartBeatRequest request);
}
