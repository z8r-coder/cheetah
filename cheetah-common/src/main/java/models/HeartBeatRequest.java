package models;

import constants.HeartBeatType;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class HeartBeatRequest implements Serializable {

    HeartBeatType type;

    public HeartBeatRequest (HeartBeatType type) {
        this.type = type;
    }

    public void setType(HeartBeatType type) {
        this.type = type;
    }

    public HeartBeatType getType() {
        return type;
    }
}
