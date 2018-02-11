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

    public HeartBeatResponse (HeartBeatType type) {
        this.heartBeatType = type;
    }

    public HeartBeatType getHeartBeatType() {
        return heartBeatType;
    }

    public void setHeartBeatType(HeartBeatType heartBeatType) {
        this.heartBeatType = heartBeatType;
    }
}
