package models;

import constants.HeartBeatType;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class HeartBeatResponse implements Serializable {

    HeartBeatType heartBeatType;
    CheetahAddress cheetahAddress;

    public HeartBeatResponse (HeartBeatType type, CheetahAddress cheetahAddress) {
        this.heartBeatType = type;
        this.cheetahAddress = cheetahAddress;
    }

    public HeartBeatType getHeartBeatType() {
        return heartBeatType;
    }

    public void setHeartBeatType(HeartBeatType heartBeatType) {
        this.heartBeatType = heartBeatType;
    }

    public CheetahAddress getCheetahAddress() {
        return cheetahAddress;
    }

    public void setCheetahAddress(CheetahAddress cheetahAddress) {
        this.cheetahAddress = cheetahAddress;
    }
}
